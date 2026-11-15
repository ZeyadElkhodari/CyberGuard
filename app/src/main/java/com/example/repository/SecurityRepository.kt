package com.example.repository

import android.content.Context
import com.example.db.CyberGuardDatabase
import com.example.db.ScanHistoryEntity
import com.example.model.*
import com.example.network.ExternalSecurityService
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class SecurityRepository(context: Context) {

    private val db = CyberGuardDatabase.getInstance(context)
    private val scanHistoryDao = db.scanHistoryDao()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val findingsAdapter = moshi.adapter<List<ScanFinding>>(
        Types.newParameterizedType(List::class.java, ScanFinding::class.java)
    )
    private val providersAdapter = moshi.adapter<List<ThreatProviderInfo>>(
        Types.newParameterizedType(List::class.java, ThreatProviderInfo::class.java)
    )

    /**
     * Performs a threat intelligence link scan using the Cloudflare Worker threat-intelligence gateway
     * and local zero-trust heuristics. Operates entirely without a custom backend.
     * When external services cannot be reached, flags as inconclusive rather than false clean.
     */
    suspend fun scanUrl(rawUrl: String): ScanResult = withContext(Dispatchers.IO) {
        val trimmed = rawUrl.trim()
        val result = ExternalSecurityService.evaluateUrl(trimmed)

        // Cache result in local Room DB for real on-device history
        try {
            val scanEntity = ScanHistoryEntity(
                id = "scan-${UUID.randomUUID()}",
                target = result.target,
                type = result.type,
                score = result.score,
                riskLevel = result.riskLevel,
                overallVerdict = result.overallVerdict,
                domain = result.domain,
                findingsJson = findingsAdapter.toJson(result.findings),
                recommendation = result.recommendation,
                providersJson = providersAdapter.toJson(result.providers),
                analyzedAt = result.analyzedAt,
                isPartialAssessment = result.isPartialAssessment
            )
            scanHistoryDao.insertScan(scanEntity)
        } catch (_: Exception) {
            // Caching failure shouldn't crash the scan
        }

        result
    }

    /**
     * Records a QR code scan result directly into local Room database.
     */
    suspend fun recordQrScan(result: ScanResult) = withContext(Dispatchers.IO) {
        try {
            val scanEntity = ScanHistoryEntity(
                id = "qr-${UUID.randomUUID()}",
                target = result.target,
                type = "qr",
                score = result.score,
                riskLevel = result.riskLevel,
                overallVerdict = result.overallVerdict,
                domain = result.domain,
                findingsJson = findingsAdapter.toJson(result.findings),
                recommendation = result.recommendation,
                providersJson = providersAdapter.toJson(result.providers),
                analyzedAt = result.analyzedAt,
                isPartialAssessment = result.isPartialAssessment
            )
            scanHistoryDao.insertScan(scanEntity)
        } catch (_: Exception) {
            // Ignore
        }
    }

    /**
     * Checks password breach directly with HaveIBeenPwned's public API using zero-knowledge k-Anonymity.
     * Never sends plaintext password or complete hashes.
     */
    suspend fun checkPasswordBreach(password: String): PasswordBreachResult = withContext(Dispatchers.IO) {
        ExternalSecurityService.checkPasswordBreach(password)
    }

    /**
     * Observes local scan history from Room DB.
     */
    fun getLocalScanHistory(): Flow<List<ScanResult>> {
        return scanHistoryDao.getAllScans().map { entities ->
            entities.map { entity ->
                val findings = try {
                    findingsAdapter.fromJson(entity.findingsJson) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
                val providers = try {
                    providersAdapter.fromJson(entity.providersJson) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
                ScanResult(
                    target = entity.target,
                    type = entity.type,
                    score = entity.score,
                    riskLevel = entity.riskLevel,
                    overallVerdict = entity.overallVerdict,
                    domain = entity.domain,
                    findings = findings,
                    recommendation = entity.recommendation,
                    analyzedAt = entity.analyzedAt,
                    isPartialAssessment = entity.isPartialAssessment,
                    providers = providers
                )
            }
        }
    }

    /**
     * Reconstructs actual scan metrics strictly from local Room database records.
     */
    suspend fun getScanMetrics(): ScanMetrics = withContext(Dispatchers.IO) {
        val totalScans = scanHistoryDao.countAll()
        val urlScans = scanHistoryDao.countUrlScans()
        val urlThreats = scanHistoryDao.countUrlThreats()
        val avgUrlScore = scanHistoryDao.getAverageUrlScore()?.toInt() ?: 0
        val qrScans = scanHistoryDao.countQrScans()
        val qrThreats = scanHistoryDao.countQrThreats()

        ScanMetrics(
            totalScans = totalScans,
            urlScansCount = urlScans,
            urlThreatsCount = urlThreats,
            urlAverageScore = avgUrlScore,
            qrScansCount = qrScans,
            qrThreatsCount = qrThreats,
            totalThreatsBlocked = urlThreats + qrThreats
        )
    }

    suspend fun clearScanHistory() = withContext(Dispatchers.IO) {
        scanHistoryDao.clearAll()
    }
}

data class ScanMetrics(
    val totalScans: Int,
    val urlScansCount: Int,
    val urlThreatsCount: Int,
    val urlAverageScore: Int,
    val qrScansCount: Int,
    val qrThreatsCount: Int,
    val totalThreatsBlocked: Int
)

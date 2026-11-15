/** CyberGuard threat-intelligence gateway.
 * The three approved providers are queried concurrently. A provider failure never cancels peers.
 * Secrets are configured only in Cloudflare Workers settings.
 */
const HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, GET, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type",
  "Content-Type": "application/json; charset=utf-8",
  "Cache-Control": "no-store",
};
const json = (body, status = 200) => new Response(JSON.stringify(body), { status, headers: HEADERS });
const result = (name, status, verdict, details, tags = []) => ({ name, status, verdict, details, tags });
const fetchOnceWithTimeout = async (url, init = {}, timeoutMs = 8000) => {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);
  try {
    const response = await fetch(url, { ...init, signal: controller.signal });
    const body = await response.text();
    return new Response(body, {
      status: response.status,
      statusText: response.statusText,
      headers: response.headers
    });
  } finally {
    clearTimeout(timer);
  }
};
const timeoutFetch = async (url, init = {}, timeoutMs = 10000) => {
  let lastError;
  for (let attempt = 0; attempt < 3; attempt++) {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), timeoutMs);
    try {
      const response = await fetch(url, { ...init, signal: controller.signal });
      // Keep the abort timer active while consuming the response body, not just until headers arrive.
      const body = await response.text();
      const buffered = new Response(body, {
        status: response.status,
        statusText: response.statusText,
        headers: response.headers
      });
      if (![502, 503, 504].includes(buffered.status) || attempt === 2) return buffered;
      lastError = new Error(`Provider HTTP ${buffered.status}`);
    } catch (error) {
      lastError = error;
      if (attempt === 2 || (error?.name !== "AbortError" && error?.name !== "TypeError")) throw error;
    } finally {
      clearTimeout(timer);
    }
    await new Promise(resolve => setTimeout(resolve, 250 * (2 ** attempt)));
  }
  throw lastError || new Error("Provider unavailable");
};
function isPrivateHost(hostname) {
  const h = hostname.toLowerCase().replace(/^\[|\]$/g, "");
  if (!h || h === "localhost" || h.endsWith(".localhost") || h.endsWith(".local") ||
      h === "metadata.google.internal" || h === "metadata.google" || h === "::" || h === "::1" ||
      h === "0.0.0.0") return true;
  if (/^\d+(\.\d+){3}$/.test(h)) {
    const p = h.split(".").map(Number);
    if (p.some(n => !Number.isInteger(n) || n < 0 || n > 255)) return true;
    const [a, b, c, d] = p;
    return a === 0 || a === 10 || a === 127 || a >= 224 ||
      (a === 169 && b === 254) || (a === 172 && b >= 16 && b <= 31) ||
      (a === 192 && b === 168) || (a === 100 && b >= 64 && b <= 127) ||
      (a === 192 && b === 0 && c === 0) || (a === 198 && (b === 18 || b === 19)) ||
      (a === 255 && b === 255 && c === 255 && d === 255);
  }
  if (h.includes(":")) {
    // IPv6 loopback, unspecified, unique-local, link-local, multicast and documentation ranges.
    if (h === "::" || h === "::1" || /^f[cd]/.test(h) || /^fe[89ab]/.test(h) ||
        /^ff/.test(h) || h.startsWith("2001:db8:")) return true;
    const mapped = h.match(/^::ffff:([0-9a-f]{1,4}):([0-9a-f]{1,4})$/);
    if (mapped) {
      const value = (parseInt(mapped[1], 16) * 65536) + parseInt(mapped[2], 16);
      return isPrivateHost([value >>> 24, (value >>> 16) & 255, (value >>> 8) & 255, value & 255].join("."));
    }
    return false;
  }
  return false;
}
function parsePublicUrl(value) {
  let u;
  try { u = new URL(value); } catch { return null; }
  if (!["http:", "https:"].includes(u.protocol) || u.username || u.password ||
      !u.hostname || u.port && (Number(u.port) < 1 || Number(u.port) > 65535)) return null;
  const h = u.hostname.toLowerCase();
  if (isPrivateHost(h) || h.endsWith(".") || h.includes("..")) return null;
  return u;
}
function httpFailure(name, response) {
  if (response.status === 429) return result(name, "rate_limited", "Service busy", "يوجد ضغط على الخدمة من باقي المستخدمين أو تم بلوغ حد الطلبات. لم تتأثر بقية خدمات الفحص.", ["HTTP 429"]);
  if (response.status === 401 || response.status === 403) return result(name, "configuration_error", "Provider configuration error", "تعذر اعتماد مفتاح الخدمة أو أن صلاحياته/حصته لا تسمح بالطلب.", [`HTTP ${response.status}`]);
  if (response.status >= 500) return result(name, "unavailable", "Service temporarily unavailable", "الخدمة الخارجية لا تستجيب حاليًا. بقية خدمات الفحص مستمرة.", [`HTTP ${response.status}`]);
  return result(name, "unavailable", "Lookup unavailable", `Provider returned HTTP ${response.status}.`, [`HTTP ${response.status}`]);
}
async function safeBrowsing(url, env) {
  const name = "Google Safe Browsing";
  if (!env.GOOGLE_SAFE_BROWSING_API_KEY) return result(name, "unconfigured", "Not configured", "مفتاح Google Safe Browsing غير مضبوط على Worker.");
  try {
    const r = await timeoutFetch(`https://safebrowsing.googleapis.com/v4/threatMatches:find?key=${encodeURIComponent(env.GOOGLE_SAFE_BROWSING_API_KEY)}`, {
      method: "POST", headers: { "content-type": "application/json" },
      body: JSON.stringify({ client: { clientId: "cyberguard", clientVersion: "1.0" }, threatInfo: {
        threatTypes: ["MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"],
        platformTypes: ["ANY_PLATFORM"], threatEntryTypes: ["URL"], threatEntries: [{ url }]
      } })
    });
    if (!r.ok) return httpFailure(name, r);
    const d = await r.json();
    return d.matches?.length
      ? result(name, "malicious", "Threat match", d.matches.map(x => x.threatType || "Unsafe URL").join(", "), ["Google threat list"])
      : result(name, "clean", "No match", "لم تُرجع قاعدة Google تطابقًا لهذا الرابط.");
  } catch (e) {
    const detail = e?.name === "AbortError" ? "انتهت مهلة الاتصال بالخدمة."
      : e?.name === "TypeError" ? "تعذر الوصول إلى مزود الخدمة عبر الشبكة أو DNS."
      : "تعذر إكمال الاتصال بمزود الخدمة.";
    return result(name, "unavailable", "Lookup failed", detail, [e?.name || "Network error"]);
  }
}
async function urlhaus(url, env) {
  const name = "URLhaus";
  if (!env.URLHAUS_AUTH_KEY) return result(name, "unconfigured", "Not configured", "مفتاح URLhaus غير مضبوط على Worker.");
  try {
    const r = await timeoutFetch("https://urlhaus-api.abuse.ch/v1/url/", {
      method: "POST", headers: {
        "Auth-Key": env.URLHAUS_AUTH_KEY,
        "content-type": "application/x-www-form-urlencoded",
        "accept": "application/json"
      },
      body: new URLSearchParams({ url })
    });
    if (!r.ok) return httpFailure(name, r);
    const d = await r.json();
    if (d.query_status === "ok") return result(name, "malicious", "Listed malware URL", `Threat: ${d.threat || "malware"}; status: ${d.url_status || "unknown"}.`, [d.threat || "malware"]);
    if (d.query_status === "no_results") return result(name, "clean", "No match", "URL is not listed in URLhaus.");
    return result(name, "unavailable", "Inconclusive", `URLhaus response: ${d.query_status || "unknown"}.`);
  } catch (e) {
    const detail = e?.name === "AbortError" ? "انتهت مهلة الاتصال بالخدمة."
      : e?.name === "TypeError" ? "تعذر الوصول إلى مزود الخدمة عبر الشبكة أو DNS."
      : "تعذر إكمال الاتصال بمزود الخدمة.";
    return result(name, "unavailable", "Lookup failed", detail, [e?.name || "Network error"]);
  }
}
async function virusTotal(url, env) {
  const name = "VirusTotal";
  if (!env.VIRUSTOTAL_API_KEY) return result(name, "unconfigured", "Not configured", "مفتاح VirusTotal غير مضبوط على Worker.");
  try {
    // URL identifier: base64url of the exact URL without padding.
    const id = btoa(unescape(encodeURIComponent(url))).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
    const r = await timeoutFetch(`https://www.virustotal.com/api/v3/urls/${id}`, { headers: { "x-apikey": env.VIRUSTOTAL_API_KEY, accept: "application/json" } });
    if (r.status === 404) return result(name, "not_indexed", "Not indexed", "لم يسبق فهرسة الرابط في VirusTotal؛ لم نرسل الرابط لإنشاء تحليل جديد.");
    if (!r.ok) return httpFailure(name, r);
    const d = await r.json();
    const s = d.data?.attributes?.last_analysis_stats || {};
    const bad = (s.malicious || 0) + (s.suspicious || 0);
    const total = Object.values(s).reduce((a, b) => a + (Number(b) || 0), 0);
    return bad
      ? result(name, "malicious", `${bad}/${total} engines flagged`, `Malicious: ${s.malicious || 0}, suspicious: ${s.suspicious || 0}, harmless: ${s.harmless || 0}.`, ["Multi-engine detection"])
      : total > 0
        ? result(name, "clean", `0/${total} flagged`, `لم ترصد المحركات المتاحة مؤشرات ضارة في آخر تحليل (${total} محركًا).`)
        : result(name, "unavailable", "No usable analysis", "لا تتوفر نتيجة تحليل مكتملة لهذا الرابط.");
  } catch (e) {
    const detail = e?.name === "AbortError" ? "انتهت مهلة الاتصال بالخدمة."
      : e?.name === "TypeError" ? "تعذر الوصول إلى مزود الخدمة عبر الشبكة أو DNS."
      : "تعذر إكمال الاتصال بمزود الخدمة.";
    return result(name, "unavailable", "Lookup failed", detail, [e?.name || "Network error"]);
  }
}

export default {
  async fetch(request, env) {
    if (request.method === "OPTIONS") return new Response(null, { headers: HEADERS });
    const u = new URL(request.url);
    if (request.method === "GET" && u.pathname === "/health") {
      const rateLimiterConfigured = !!env.RATE_LIMITER && typeof env.RATE_LIMITER.limit === "function";
      return json({
        ok: true,
        service: "cyberguard-api",
        version: "3.3",
        providers: 3,
        rateLimiterConfigured
      }, rateLimiterConfigured ? 200 : 503);
    }
    if (request.method !== "POST" || u.pathname !== "/api/scan/url") return json({ error: "Not found" }, 404);
    const declaredLength = Number(request.headers.get("content-length") || 0);
    if (declaredLength > 4096) return json({ error: "Request too large" }, 413);
    let rawBody;
    try {
      const reader = request.body?.getReader();
      if (!reader) return json({ error: "Missing request body" }, 400);
      const chunks = [];
      let total = 0;
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        total += value.byteLength;
        if (total > 4096) {
          await reader.cancel();
          return json({ error: "Request too large" }, 413);
        }
        chunks.push(value);
      }
      const bytes = new Uint8Array(total);
      let offset = 0;
      for (const chunk of chunks) { bytes.set(chunk, offset); offset += chunk.byteLength; }
      rawBody = new TextDecoder().decode(bytes);
    } catch { return json({ error: "Invalid request body" }, 400); }
    let body;
    try { body = JSON.parse(rawBody); } catch { return json({ error: "Invalid JSON" }, 400); }
    const raw = typeof body.url === "string" ? body.url.trim() : "";
    if (!raw || raw.length > 2048) return json({ error: "URL is required (max 2048 chars)" }, 400);
    const parsed = parsePublicUrl(raw);
    if (!parsed) return json({ error: "Only valid public HTTP/HTTPS URLs are accepted" }, 400);

    // Configure a Cloudflare Workers Rate Limiting binding named RATE_LIMITER.
    // Fail closed rather than silently exposing provider quotas without rate limiting.
    if (!env.RATE_LIMITER || typeof env.RATE_LIMITER.limit !== "function") {
      return json({ error: "Rate limiter is not configured" }, 503);
    }
    const clientKey = request.headers.get("CF-Connecting-IP") || "unknown-client";
    try {
      const { success } = await env.RATE_LIMITER.limit({ key: clientKey });
      if (!success) return json({ error: "Rate limit exceeded. Try again later." }, 429);
    } catch {
      return json({ error: "Rate limiting service unavailable" }, 503);
    }
    // Isolated concurrent calls: one provider's quota/timeout cannot stop the other providers.
    const providers = await Promise.all([
      safeBrowsing(parsed.href, env),
      urlhaus(parsed.href, env),
      virusTotal(parsed.href, env),
    ]);
    const completed = providers.filter(p => ["clean", "malicious"].includes(p.status)).length;
    return json({ ok: true, target: parsed.href, checkedAt: new Date().toISOString(), completed, total: 3, providers });
  }
};

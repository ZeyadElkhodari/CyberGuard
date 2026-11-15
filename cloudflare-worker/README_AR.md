# نشر CyberGuard Cloudflare Worker

## مصدر الإعداد الصحيح

يجب تشغيل أوامر Wrangler من مجلد المشروع الرئيسي `CyberGuard/`؛ لأن ملف الإعداد المعتمد هو `wrangler.toml` في هذا المجلد، وهو يحدد:

- ملف التشغيل: `worker.js`
- اسم Worker: `cyberguard-api`
- ربط Rate Limiting: `RATE_LIMITER`
- الحد: 10 طلبات خلال 60 ثانية لكل مفتاح عميل، في كل موقع Cloudflare

الملف `cloudflare-worker/index.js` نسخة مطابقة من `worker.js` للاحتفاظ بنسخة داخل مجلد Worker. ملف النشر المعتمد هو `worker.js` المحدد في `wrangler.toml`.

## 1. تسجيل الدخول والتحقق من Wrangler

تحتاج إلى Node.js واتصال إنترنت وحساب Cloudflare لديه صلاحية نشر Workers.

من PowerShell أو Terminal:

```powershell
cd CyberGuard
npx wrangler@latest --version
npx wrangler@latest login
npx wrangler@latest whoami
```

يجب استخدام Wrangler 4.36.0 أو أحدث لدعم إعداد Rate Limiting الحالي.

## 2. إضافة مفاتيح الخدمات كـ Secrets

نفّذ الأوامر التالية، وأدخل قيمة كل مفتاح عند الطلب. لا تضع المفاتيح في ملفات المشروع أو تطبيق Android:

```powershell
npx wrangler@latest secret put GOOGLE_SAFE_BROWSING_API_KEY
npx wrangler@latest secret put URLHAUS_AUTH_KEY
npx wrangler@latest secret put VIRUSTOTAL_API_KEY
```

إذا كانت الأسرار موجودة بالفعل على Worker نفسه، فلا حاجة لإعادة إدخالها إلا إذا كانت غير صحيحة أو منتهية.

## 3. نشر Worker مع RATE_LIMITER

من مجلد `CyberGuard/` نفسه:

```powershell
npx wrangler@latest deploy --config wrangler.toml
```

يجب أن يكتمل النشر دون أخطاء. ملف `wrangler.toml` هو مصدر الحقيقة للربط؛ لا تنشئ متغيرًا نصيًا باسم `RATE_LIMITER` في Secrets أو Variables. إنه Binding من نوع Rate Limiting، وليس مفتاحًا سريًا.

## 4. التحقق بعد النشر

افتح عنوان Worker الفعلي الذي يعرضه Wrangler، ثم أضف `/health`:

```powershell
Invoke-RestMethod "https://YOUR-WORKER-SUBDOMAIN.workers.dev/health"
```

النتيجة المطلوبة:

```json
{
  "ok": true,
  "service": "cyberguard-api",
  "version": "3.3",
  "providers": 3,
  "rateLimiterConfigured": true
}
```

إذا أعاد `/health` الحالة 503 أو كانت `rateLimiterConfigured` تساوي `false`، فهذا يعني أن النسخة المنشورة لا تحتوي على Binding المطلوب. راجع أنك نشرت من مجلد المشروع الصحيح وباستخدام ملف `wrangler.toml` الصحيح.

## 5. اختبار فحص الرابط

```powershell
$body = @{ url = "https://example.com" } | ConvertTo-Json
Invoke-RestMethod -Method Post `
  -Uri "https://YOUR-WORKER-SUBDOMAIN.workers.dev/api/scan/url" `
  -ContentType "application/json" `
  -Body $body
```

يجب أن يحتوي الرد على `providers` بثلاث نتائج مستقلة: Google Safe Browsing وURLhaus وVirusTotal. قد تكون نتيجة أي مزود `unconfigured` أو `unavailable` إذا كان مفتاحه غير مضبوط أو المزود لا يستجيب؛ هذا لا يعني نجاح فحص ذلك المزود.

## ملاحظات مهمة

- `/health` يتحقق من وجود الـ Binding في بيئة Worker المنشورة، ولا يختبر صلاحية مفاتيح الخدمات الخارجية.
- `RATE_LIMITER` يطبق حدًا تقريبيًا محليًا لكل موقع Cloudflare، وليس حدًا عالميًا صارمًا. قد تتجاوز بعض الطلبات الحد أثناء تزامن العدادات.
- المفتاح الحالي للحد هو عنوان IP الذي يضيفه Cloudflare (`CF-Connecting-IP`). قد يشترك مستخدمون خلف شبكة شركة أو مزود اتصال واحد في الحد نفسه.
- لا تعطّل فحص Rate Limiting لتجاوز HTTP 503. أصلح Binding ثم أعد النشر.
- لا تنشر أسرار الخدمات في Git أو ZIP أو تطبيق Android.

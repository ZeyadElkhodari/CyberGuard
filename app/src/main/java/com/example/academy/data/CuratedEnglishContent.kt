package com.example.academy.data

/** Reviewed offline English lesson library. Article IDs are stable and match academy_seed.json. */
object CuratedEnglishContent {
    fun getArticle(id: String): AcademyTranslations.TranslatedArticle? = ARTICLES[id]
    fun getQuestion(id: String): AcademyTranslations.TranslatedQuestion? = QUESTIONS[id]

    fun fallbackTranslate(arabicText: String): String {
        val normalized = arabicText.trim()
        if (normalized == "نعم" || normalized == "أيوه" || normalized == "أجل") return "Yes"
        if (normalized == "لا" || normalized == "كلا") return "No"
        return "This lesson explains a cybersecurity risk, how to recognize it, and practical steps to stay safe."
    }

    private fun article(title: String, intro: String, explanation: String, scenario: String,
                        warnings: List<String>, steps: List<String>, recovery: String, summary: String) =
        AcademyTranslations.TranslatedArticle(title, intro, explanation, scenario, warnings, steps, recovery, summary)

    private val ARTICLES = mapOf(
"art-1-1" to article(
            "What Is Phishing? The Bait Behind the Message",
            "Learn how criminals use convincing messages, alarming warnings, and attractive prizes to make people reveal information or open malicious links.",
            "Phishing is a deceptive message designed to look as if it came from a trusted bank, delivery company, employer, or friend. The sender creates urgency or excitement and directs you to a fake page that collects passwords, card details, or verification codes.",
            "Ahmed receives a text saying his bank account will be suspended unless he verifies it immediately. The link opens a page that copies the bank website and asks for his card number and one-time code.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-2" to article(
            "The Pending Parcel and Customs Fee Scam",
            "A fake delivery notice may arrive while you are genuinely expecting a package. Learn why a tiny fee can be used to steal much more than the requested amount.",
            "Fraudsters send fake courier or customs messages with a payment link. The small amount lowers suspicion, while the fake checkout collects the full card number, expiry date, security code, and sometimes an OTP.",
            "Mona is waiting for an online order and receives a message asking for a small release fee. The page looks like a postal service portal, so she enters her card details without checking the sender or domain.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-3" to article(
            "WhatsApp Messages from Friends: “Send Me the Code!”",
            "A familiar name or profile photo does not prove that a message is genuine. Learn how account takeovers are used to trick contacts into sharing verification codes.",
            "An attacker who controls a friend’s account can impersonate them and ask for an SMS or WhatsApp verification code. That code is intended only for the account owner and can let the attacker register or recover an account.",
            "A message arrives from a cousin saying a code was sent to your phone by mistake and asking you to forward it. The code is actually for registering your WhatsApp account on another device.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-4" to article(
            "How to Read a Link Before You Click: Understanding URLs",
            "Learn to identify the actual website behind a link, including misleading subdomains, extra words, and look-alike characters.",
            "Read the registered domain from right to left. In `facebook.com.login-example.net`, the controlling domain is `login-example.net`, not Facebook. HTTPS and a padlock encrypt the connection but do not prove that a site is legitimate.",
            "Sara receives a link that begins with a familiar brand name but ends with an unrelated domain. She checks the full address and notices the real domain does not belong to the brand.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-5" to article(
            "Fake Job Offers: High Daily Pay for Simple Tasks",
            "Scammers target job seekers with unusually high pay, easy work, and promises of immediate acceptance.",
            "A fake recruiter may ask for registration, training, identity-verification, or withdrawal fees. Some schemes also collect identity documents or bank details under the pretext of onboarding.",
            "A job seeker is promised a large daily payment for rating hotels. Before starting, the recruiter asks for a fee to activate the account and refuses to provide a verifiable company address.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-6" to article(
            "Fake Bank Alerts: “Your Card Has Been Blocked”",
            "Learn to recognize messages that imitate bank security alerts and pressure you into revealing sensitive information.",
            "Fraudsters use account-blocking warnings to create panic. They direct victims to fake support numbers or websites and request card data, passwords, PINs, or OTPs.",
            "A customer receives a text claiming the card is blocked and must be reactivated through a link. The customer independently calls the number printed on the physical card and confirms the alert is fake.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-7" to article(
            "Fake Charity and Donation Appeals",
            "Fraudulent donation campaigns often appear during emergencies, holidays, and religious or seasonal giving periods.",
            "Scammers copy charity names, photographs, and stories, then redirect donations to personal wallets or accounts. Emotional urgency can discourage people from checking whether the organization is registered.",
            "A social media post asks for urgent donations for a family and provides a personal wallet number. The poster cannot provide the charity’s registration details or an independently verifiable campaign.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-8" to article(
            "Malicious Ringback-Tone Offers and Top-Up Codes",
            "Promotions involving free ringback tones, recharge cards, or gaming credits can be used to steal accounts and payment value.",
            "A fake promotion may ask you to enter a phone number, share a code, subscribe to a paid service, or install an unknown application. The promised reward is only bait for collecting credentials or activating charges.",
            "A Facebook post promises free mobile credit. The page asks the user to enter a phone number and an SMS code, which actually confirms a paid subscription or account action.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-9" to article(
            "Fake Hotel and Booking Confirmation Emails",
            "Learn how counterfeit booking emails imitate real reservations and use attachments or links to steal payment information.",
            "Attackers copy hotel branding and booking details to make a message appear authentic. They may claim a reservation needs payment confirmation or ask you to download an attachment.",
            "A traveler receives an email saying a hotel booking will be cancelled unless payment details are updated. The traveler checks the reservation through the hotel’s official app instead of using the email link.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-1-10" to article(
            "How to Report a Scam Message and Protect Others",
            "A safe response to a scam includes preserving useful evidence, reporting it through official channels, and warning others without spreading the malicious link.",
            "Reporting helps service providers and authorities identify repeated campaigns. Keep screenshots, sender details, timestamps, and transaction references, but do not forward active malicious links or expose other people’s private data.",
            "After receiving a fake bank message, a user saves a screenshot, reports the sender through the messaging app and bank, blocks the number, and shares a warning that does not include the clickable link.",
            listOf("A message creates panic or excitement and demands immediate action through a link or phone number.", "The sender asks for card details, passwords, OTPs, personal documents, or a small payment to unlock a supposed benefit.", "The sender address, phone number, or website domain differs from the organization’s verified channels."),
            listOf("Do not use the message link or phone number; open the official app or type the known website address yourself.", "Never disclose an OTP, PIN, password, or full card security details to a caller or message sender.", "Report and block the sender, and warn others without forwarding a clickable malicious link."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-1" to article(
            "What Is Social Engineering? Hacking People, Not Servers",
            "Understand how attackers exploit trust, fear, helpfulness, curiosity, and authority to make people bypass security controls.",
            "Social engineering is psychological manipulation used to obtain information, access, or actions. The attacker may impersonate a colleague, official, technician, or relative and rely on pressure rather than technical exploitation.",
            "A visitor claims to be a technician and asks a clinic receptionist to connect an unknown USB drive. The receptionist verifies the visit with the IT department instead of accepting the story.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-2" to article(
            "The Urgency Trap: “You Have One Minute to Act”",
            "Urgency is a common manipulation technique because it reduces the time available to think and verify.",
            "Attackers invent deadlines, account closures, emergencies, or expiring offers to push victims into acting before checking the request. A legitimate process should allow independent verification.",
            "A caller says a bank transfer must be approved within one minute. The customer ends the call and contacts the bank using its official number.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-3" to article(
            "Exploiting Kindness: The Helpful Stranger Trap",
            "Learn how criminals use sympathy and the desire to help to gain access, money, or sensitive information.",
            "An attacker may invent a medical emergency, workplace crisis, or urgent favor. The story may be emotionally convincing, but the request still needs to follow normal identity and authorization checks.",
            "A stranger asks an employee to use a company computer to print a file because of an urgent family problem. The employee offers safe public printing options and does not connect unknown media to a work device.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-4" to article(
            "Impersonating Technical Support: “I’m Here to Fix the Network”",
            "Unexpected technicians and support agents may use technical language and professional clothing to appear trustworthy.",
            "Attackers impersonate internet providers, maintenance staff, or IT support to enter restricted areas, connect devices, or obtain passwords. Verify work orders and identity through a known internal contact.",
            "A person arrives claiming to replace a router and asks for the Wi-Fi administrator password. The employee confirms the service appointment with the provider before allowing access.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-5" to article(
            "CEO Fraud: Fake Instructions from the Boss",
            "Business email compromise and executive impersonation exploit hierarchy, confidentiality, and pressure to make unauthorized payments.",
            "A fraudster imitates an executive or supplier and requests an urgent transfer, gift cards, payroll change, or confidential file. Email display names can be forged, and compromised accounts may look genuine.",
            "An accounts employee receives an urgent message supposedly from the CEO requesting a confidential transfer. The employee follows the company’s independent callback and dual-approval procedure.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-6" to article(
            "Found a USB Drive? Do Not Plug It In (Baiting)",
            "Unknown USB devices can be deliberately left where employees or visitors are likely to find and connect them.",
            "Baiting uses curiosity or helpfulness to get someone to connect an untrusted device. It may contain malware, malicious shortcuts, or files designed to exploit vulnerable software.",
            "An employee finds a branded USB drive in the parking lot. Instead of plugging it into a work computer, they hand it to IT or security for safe handling.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-7" to article(
            "Fake Sextortion Messages: “We Hacked Your Camera”",
            "Many extortion emails claim to have compromising recordings and demand payment, even when the sender has no evidence.",
            "Scammers use generic threats, leaked passwords, or intimidating technical claims to create fear. A claim alone is not proof that a camera or device was compromised. Do not pay or reply; secure accounts and preserve the message.",
            "A user receives an email claiming that a webcam recording will be published unless cryptocurrency is sent. The user does not pay, changes any reused password, enables MFA, and reports the message.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-8" to article(
            "Quid Pro Quo: A “Free Service” with a Hidden Cost",
            "A promised service, repair, gift, or technical favor can be used to obtain credentials or install unsafe software.",
            "Quid pro quo attacks offer something in exchange for access or information. The attacker may pose as support and request a password, remote-control session, or installation of an unapproved tool.",
            "A caller offers to speed up a computer for free but asks the employee to install a remote-access program. The employee declines and contacts the organization’s verified IT help desk.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-9" to article(
            "Shoulder Surfing: Watching Your Screen and Keyboard",
            "Sensitive information can be exposed simply by observing a screen, keyboard, or paper in a public place.",
            "Shoulder surfing is the act of watching someone enter a PIN, password, or private information. Cameras and reflective surfaces can also reveal what is typed or displayed.",
            "A commuter enters a banking PIN while someone stands close behind. They shield the keypad, move to a safer position, and avoid viewing account details on a shared screen.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-2-10" to article(
            "Your Psychological Shield: Recognize and Stop Manipulation",
            "Build a repeatable method for responding to pressure, impersonation, and unusual requests.",
            "Pause before acting, verify identity through a separate trusted channel, follow established procedures, and refuse requests for passwords or one-time codes. A real emergency does not make unsafe verification acceptable.",
            "An employee receives an urgent request from a familiar name asking for a password reset code. They call the colleague using a saved number and discover the account was impersonated.",
            listOf("The person invokes authority, an emergency, guilt, secrecy, or an unusually tight deadline.", "They ask you to bypass normal approval, reveal credentials, transfer money, or grant physical or remote access.", "Their identity or request cannot be confirmed through a separate trusted channel."),
            listOf("Pause and verify identity using a saved phone number, internal directory, or known support channel.", "Follow written approval and access procedures even when the requester claims to be senior or urgent.", "Refuse requests for passwords and OTPs, and report suspicious approaches to the responsible security team."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-1" to article(
            "Anatomy of a Fake Facebook or Instagram Store",
            "Learn how fraudulent social media shops use copied branding, fake followers, and controlled comments to appear legitimate.",
            "A page can buy followers, change its name, copy product photos, and delete complaints. A large audience or polished design is not proof of a real business.",
            "A shopper sees a luxury bag advertised at an unusually large discount. They check the page history, independent reviews, business registration, and payment options before ordering.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-2" to article(
            "Too-Good-to-Be-True Prices: Greed as a Scam Trigger",
            "Unrealistic discounts are often used to make shoppers ignore normal checks and pay quickly.",
            "Scammers advertise expensive products at prices far below market value, then demand advance payment or send counterfeit goods. Compare prices and verify the seller before paying.",
            "A phone listed at a fraction of its normal price is available only by instant wallet transfer. The buyer refuses to pay before verifying the seller and product.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-3" to article(
            "Cash on Delivery and Inspection: Safer Online Shopping",
            "Cash on delivery can reduce some payment risks, but it does not guarantee that the parcel contains the advertised product.",
            "Use reputable sellers and couriers, inspect the package where the service permits it, and understand the return policy. Avoid paying extra fees through links sent by unknown numbers.",
            "A customer orders a device from a known retailer and checks the package condition and order details at delivery before confirming receipt.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-4" to article(
            "Clone Stores: Fake Websites That Copy Real Shops",
            "A copied website may look identical to a trusted retailer while using a different domain controlled by criminals.",
            "Fraudsters copy logos, layouts, product photos, and checkout pages. Check the exact domain, independently navigate to the retailer, and avoid links from unsolicited ads or messages.",
            "A shopper opens a sponsored link to a store with a familiar logo but a misspelled domain. They close it and type the retailer’s known address themselves.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-5" to article(
            "Miracle Health and Beauty Products: The Fake-Cure Trap",
            "Fraudulent sellers use exaggerated health and beauty claims to pressure customers into buying unverified products.",
            "Claims of guaranteed cures, dramatic results, or secret ingredients without credible evidence are warning signs. Check product authorization and consult qualified professionals for medical decisions.",
            "A social media seller promises a guaranteed cure and offers a discount only if the customer pays immediately. The customer checks the product with a qualified professional and an official regulator.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-6" to article(
            "The Deposit Scam: “Pay a Small Reservation Fee First”",
            "Advance deposits can disappear when a seller has no real product, inventory, or intention to deliver.",
            "Scammers create urgency around limited stock and request a reservation payment to a personal wallet. Verify the seller, terms, and refund policy before sending money.",
            "A seller asks for a deposit to reserve a discounted appliance but refuses to provide a verifiable address or invoice. The buyer walks away and uses a reputable retailer.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-7" to article(
            "Buying and Selling Used Items Safely on Marketplaces",
            "Peer-to-peer marketplaces can expose buyers and sellers to fake payment screenshots, courier tricks, and unsafe meeting arrangements.",
            "Never rely on a screenshot as proof of payment. Confirm funds inside your own banking app, keep conversations on-platform, and meet in a safe public place when appropriate.",
            "A buyer sends a payment screenshot and asks the seller to hand over a laptop immediately. The seller checks the actual account balance and sees no payment.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-8" to article(
            "Spin-the-Wheel Games and Fake Discount Promotions",
            "Gamified promotions can collect personal information, trigger unwanted subscriptions, or spread malicious links.",
            "A fake prize wheel may require a phone number, contact list access, repeated sharing, or a payment to claim a prize. Read the terms and verify the promotion on the company’s official site.",
            "A user wins a large coupon on an unfamiliar page but is asked to share it with twenty contacts and enter an SMS code. They close the page and check the retailer’s official promotions.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-9" to article(
            "Your Consumer Rights and How to File a Complaint",
            "Knowing how to document a purchase and contact consumer-protection channels improves your ability to resolve disputes.",
            "Keep invoices, order confirmations, seller details, delivery records, and written communications. Use official consumer-protection channels and payment-provider dispute processes rather than relying on threats or public arguments.",
            "A customer receives a product that differs from the listing. They preserve the advertisement and invoice, contact the seller in writing, and submit a complaint through the official channel when unresolved.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-3-10" to article(
            "How to Recover Your Money After a Fake-Store Purchase",
            "Act quickly after discovering a fraudulent purchase to improve the chance of stopping or disputing a transaction.",
            "Contact your bank or payment provider immediately, request a transaction dispute or card block when appropriate, change exposed passwords, and report the seller. Recovery is not guaranteed, so preserve all evidence.",
            "After paying a fake store, a shopper contacts the bank through its official number, reports the transaction, saves the chat and receipt, and files a complaint with the relevant authority.",
            listOf("The price or promised result is unrealistic, or the seller pressures you to pay immediately.", "The seller requests an advance transfer to a personal wallet or refuses invoices, inspection, or clear return terms.", "The store has a new or inconsistent history, copied branding, unverifiable contact details, or suspicious links."),
            listOf("Check the exact domain, seller identity, independent reviews, and official business contact details.", "Use a payment method with buyer protection and never treat a screenshot as proof that funds arrived.", "Keep the listing, messages, invoice, and delivery evidence; understand return and complaint procedures."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-1" to article(
            "Pyramid Marketing: The Business Opportunity Illusion",
            "Understand how pyramid schemes disguise recruitment-based money transfers as entrepreneurship or financial freedom.",
            "In a pyramid scheme, participants mainly earn by recruiting new members who pay to join. The structure requires an ever-growing stream of recruits and becomes mathematically unsustainable.",
            "Tarek is promised high income after paying a membership fee and recruiting five friends. The presentation focuses on recruitment rewards rather than real customer demand for a product.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-2" to article(
            "Legitimate Direct Selling vs. Illegal Pyramid Schemes",
            "Not every direct-selling business is a pyramid scheme. The key distinction is whether income comes from genuine retail sales or recruitment fees.",
            "A legitimate model should offer real products at reasonable prices and allow earnings from sales to genuine customers. Be cautious when joining fees, inventory purchases, or recruiting dominate the compensation plan.",
            "A company explains that distributors earn only when they recruit others. A legitimate seller instead checks product demand, refund terms, and whether retail sales can generate income without recruitment.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-3" to article(
            "Inside the Hotel Seminars and High-Energy Recruitment Events",
            "Large presentations can use music, testimonials, staged success stories, and group pressure to make a risky opportunity feel proven.",
            "Emotional events are not independent evidence of profitability. Organizers may showcase exceptional stories while hiding typical losses, expenses, and the number of participants who leave without earning.",
            "A guest attends a hotel seminar where luxury cars and enthusiastic speakers dominate the presentation. They request written financial disclosures and leave without paying under pressure.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-4" to article(
            "The Worthless Product Trick: An Inflated Price for No Value",
            "Some schemes attach an overpriced or low-value product to a membership fee to make recruitment payments appear like ordinary sales.",
            "Compare the product with independent market alternatives. If the price is far above its real value and participants buy mainly to qualify for commissions, the product may be a cover for recruitment.",
            "Hossam is asked to buy an ordinary accessory for a very high price to activate a distributor position. He compares market prices and declines.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-5" to article(
            "Damaged Relationships: Turning Friends and Family into Prospects",
            "Recruitment schemes can pressure participants to approach relatives and friends, damaging trust and personal relationships.",
            "When income depends on recruiting people you know, social relationships become a sales pipeline. Avoid using emotional pressure or presenting uncertain earnings as guaranteed.",
            "A participant is told to prepare a list of fifty relatives and invite them to a private presentation. They decide not to risk family relationships for an unverified opportunity.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-6" to article(
            "The Recruitment Tree: Why Most Participants Lose Money",
            "The mathematics of exponential recruitment makes pyramid structures impossible to sustain for everyone.",
            "If every member must recruit several new members, the required population grows rapidly at each level. Eventually there are not enough new participants, and those who joined later are most exposed to losses.",
            "A recruiter demonstrates a plan requiring each person to bring five more people. A participant calculates the number of recruits needed after several levels and recognizes that the promise cannot scale.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-7" to article(
            "Online Forex Courses and Trading Pyramid Schemes",
            "Some online trading offers combine education, referral commissions, and unrealistic profit claims to disguise recruitment schemes.",
            "Real trading involves risk and no course can guarantee returns. Be wary when the main income comes from selling memberships or recruiting investors rather than transparent, regulated services.",
            "A course promoter guarantees monthly profits and pays commissions for each new student. The learner checks regulatory status, fee structure, and independent evidence before committing money.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-8" to article(
            "Rebranding and Disappearing: How Pyramid Schemes Return",
            "Fraudulent networks may close, change names, and relaunch with a new logo or product to avoid complaints and continue recruiting.",
            "A new brand does not erase the history of its organizers. Check company registration, leadership, complaints, legal notices, and the actual compensation plan before joining.",
            "A group that previously collapsed reappears under a different name with the same recruitment structure. A potential member checks the organizers’ history and declines.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-9" to article(
            "Emotional Pressure: “You Are Afraid of Success”",
            "Recruiters may shame people who ask questions or refuse to invest, framing caution as a lack of ambition.",
            "This is a manipulation tactic. Responsible businesses answer questions about costs, risks, refunds, and realistic earnings without insults or pressure. Take time to review documents independently.",
            "When asked for written evidence, a recruiter says the prospect is negative and afraid of success. The prospect recognizes the pressure tactic and ends the discussion.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        ),
        "art-4-10" to article(
            "How to Leave Safely and Protect People Around You",
            "If you joined a suspicious scheme, focus on limiting further losses and preventing additional recruitment.",
            "Stop paying and recruiting, save contracts and payment records, review cancellation and refund rights, contact your bank or payment provider, and seek advice from relevant authorities. Do not make threats or destroy evidence.",
            "A participant decides to leave, cancels recurring payments, documents all transfers and messages, warns friends factually, and reports suspected fraud through official channels.",
            listOf("Income is described as guaranteed, unusually high, or dependent mainly on recruiting new members.", "You must pay to join, buy overpriced inventory, or recruit others to qualify for commissions.", "The organizer discourages questions, hides typical earnings and expenses, or pressures you emotionally."),
            listOf("Ask for the full compensation plan, all fees, refund terms, and typical net earnings in writing.", "Check whether real retail customers buy the product without joining the recruitment network.", "Do not borrow or pay under pressure; independently verify the company and its organizers before committing."),
            "If you already shared information or paid, contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, preserve evidence, and report the incident through official channels.",
            "Slow down, verify independently, protect credentials and payments, and report suspicious activity."
        )
        ,"art-5-1" to article(
            "Your National ID Is Not a Casual Attachment",
            "Sending both sides of your national ID over WhatsApp may feel routine when applying for a job, renting a home, or booking a service. The image can outlive the conversation and travel far beyond the person you sent it to.",
            "An identity document contains information that can help criminals impersonate you, register services, or support fraudulent applications. A chat account, phone, or print shop can be compromised, and deleting your copy does not guarantee that every recipient copy is gone.",
            "Salem sends a clear ID photo to an unverified property broker. The broker's phone is later compromised, and the image is circulated. Months later, Salem discovers mobile lines and accounts he never opened being associated with his identity.",
            listOf("An individual or informal business demands a full, clear ID image without explaining the legal or operational need.", "You are asked to post the document in a public group or send it to an unknown personal number.", "The recipient refuses to explain how the image will be stored, who can access it, or when it will be deleted."),
            listOf("Verify the organization and the reason for collecting the document before sharing anything.", "Where accepted, add a visible watermark stating the recipient, purpose, and date; do not cover legally required details unless the recipient confirms it is acceptable.", "Use an official secure upload channel, limit access, and request deletion when the stated purpose is complete."),
            "If the image may have leaked, keep evidence of the request and disclosure, contact the relevant institution, monitor for accounts or mobile lines opened in your name, and report suspected identity misuse to the appropriate authorities.",
            "Treat an ID image like a physical identity document: share it only when necessary, with a verified recipient, for a clearly limited purpose."
        ),
        "art-5-2" to article(
            "ATM Receipts and Store Bills: Small Papers, Sensitive Data",
            "A discarded cash-machine receipt or checkout slip can reveal more than you expect, especially when combined with other information about you.",
            "Receipts may expose partial card details, transaction time, account balance, merchant location, or reference numbers. A receipt alone may not enable account access, but it can help a fraudster build a convincing impersonation story when combined with other leaked data.",
            "A customer throws away an ATM receipt showing a recent balance and leaves a pharmacy bill carrying a phone number nearby. Someone collects both and later calls pretending to be a bank employee who knows the customer's recent activity.",
            listOf("Receipts containing transaction details are left beside ATMs, counters, or in public bins.", "A printed bill includes a phone number, loyalty identifier, or other personal information that was not needed.", "A caller uses details from a recent purchase or withdrawal to pressure you into confirming additional information."),
            listOf("Choose digital receipts when available and check the screen before printing financial details.", "Tear or shred receipts before disposal, especially those showing account, card, or contact information.", "Never treat knowledge of a recent transaction as proof that a caller represents your bank."),
            "Review recent transactions and enable instant transaction alerts. If you disclosed credentials or an OTP to a caller, contact your bank immediately using the official number on your card or app.",
            "A receipt is not just waste paper. Minimize what it displays and destroy sensitive details before disposal."
        ),
        "art-5-3" to article(
            "Boarding Passes and Event Tickets Reveal More Than Memories",
            "Posting a boarding pass, concert ticket, or conference badge can expose booking information that should remain private.",
            "Barcodes and booking references may encode passenger or event details and can sometimes be used with a provider's website to view or change a reservation. The exact data varies by provider, but a visible code or reference should be treated as sensitive.",
            "A traveler posts a boarding-pass photo before departure. A stranger reads the booking reference and attempts to access the reservation, creating a risk of unwanted changes or exposure of travel details.",
            listOf("A ticket photo clearly shows its barcode, QR code, booking reference, ticket number, or full name.", "A social post reveals travel dates, seat details, hotel information, or the time a home may be empty.", "A ticket or badge is shared publicly even though the provider uses its code to retrieve booking information."),
            listOf("Do not post tickets or boarding passes while the booking is active; share a photo without the document instead.", "If a ticket image must be shared, fully cover every barcode, QR code, booking reference, and personal identifier.", "Use the airline or event provider's official account security options and keep booking emails private."),
            "If a ticket or boarding pass was exposed, contact the airline or event organizer through its official channel, ask them to secure the booking, and review the reservation for unauthorized changes.",
            "Celebrate the trip or event without publishing the codes and references that control the booking."
        ),
        "art-5-4" to article(
            "Free Face Filters and Personality Quizzes Can Harvest Your Data",
            "A playful quiz or aging filter may seem harmless, but some services request access far beyond what the feature needs.",
            "A service may collect profile details, photos, contacts, device identifiers, or account permissions for advertising, profiling, or other uses described in its privacy terms. Signing in with a social account can also grant permissions that remain active after the game is forgotten.",
            "A user installs a photo-filter app that requests access to the entire photo library and contact list. The app's permissions and data practices are much broader than needed to apply one effect.",
            listOf("A simple quiz or filter requests access to contacts, private messages, or the entire photo library.", "The service requires a social-media login but gives no clear explanation of the permissions it needs.", "The privacy policy is missing, vague, or allows broad sharing with advertising or analytics partners."),
            listOf("Avoid uploading private photos or sensitive personal details to unknown quiz and filter services.", "Grant only the minimum permission needed, and deny access that is unrelated to the feature.", "Review connected apps in your Google, Apple, and social-media account settings and revoke access you no longer need."),
            "Uninstall the app, revoke its account permissions, change your password if you entered it directly into an untrusted page, and review account activity for unfamiliar access.",
            "A free filter may cost you privacy. Check permissions and data practices before sharing photos or connecting an account."
        ),
        "art-5-5" to article(
            "Why Every Shop Does Not Need Your Phone Number",
            "Cashiers sometimes ask for a phone number for discounts or loyalty points. You can pause and decide whether the benefit is worth sharing it.",
            "A phone number can connect your identity across shopping, messaging, banking, and recovery services. When collected by many businesses, it may be exposed through weakly protected databases or used for unwanted marketing and targeted scams.",
            "A customer gives her primary number at every checkout. After a retailer's marketing database is exposed, she receives messages that mention the store and imitate its promotions, making fraudulent links seem more believable.",
            listOf("A store insists that a phone number is mandatory for an ordinary purchase without explaining why.", "Your number is spoken aloud in a crowded queue or written where other customers can see it.", "You begin receiving targeted messages referencing stores or purchases where you supplied your number."),
            listOf("Ask whether the purchase can be completed without joining a loyalty program or marketing list.", "If you choose to enroll, read the consent terms and opt out of promotional messages you do not want.", "Provide the number privately rather than announcing it aloud, and keep your primary number for essential services where practical."),
            "Use the retailer's official channel to withdraw marketing consent or request correction or deletion where applicable. Block and report suspicious messages, and never use their links to manage an account.",
            "Your phone number is personal information, not a compulsory checkout detail. Share it only when the purpose and benefit are clear."
        ),
        "art-5-6" to article(
            "Public Bios Can Give Attackers the Answers They Need",
            "A profile that lists your birthday, email, phone number, school, and family details can become a ready-made research file for impersonators.",
            "Attackers combine public information from social networks and other sources to guess recovery answers, impersonate you, or craft believable messages. Security questions based on facts that friends or strangers can discover are especially weak.",
            "A user publicly lists a birthday, school, and family names. Someone combines those details with a leaked email address and sends a convincing account-recovery request to the user's contacts.",
            listOf("Your full birth date, phone number, personal email, or family details are visible to everyone.", "Recovery questions use facts that appear on your profile or can be found through a simple search.", "Unknown people reference personal details in messages and ask you to confirm more information."),
            listOf("Limit profile visibility and remove contact details and unnecessary personal facts from public bios.", "Use unique, random recovery answers and store them securely rather than using publicly known facts.", "Review account recovery options and enable multifactor authentication with methods you control."),
            "Make exposed profile details private, review recent account sessions, change reused passwords, and update recovery methods that rely on publicly known information.",
            "Small profile details can combine into a powerful identity profile. Publish less and secure account recovery independently."
        ),
        "art-5-7" to article(
            "Live Vacation Posts Can Advertise an Empty Home",
            "Real-time holiday updates can reveal where you are, how long you will be away, and whether your home is unattended.",
            "Public posts, location tags, and travel photos can disclose routines and absence patterns. This does not mean every post causes a burglary, but broadcasting precise travel plans increases information available to people who may misuse it.",
            "A family posts airport photos and live location updates throughout a week-long trip. Their public posts make the dates of their absence easy to infer.",
            listOf("Your account is public and posts reveal that everyone at home is away.", "Photos expose hotel names, boarding details, home addresses, or live location tags.", "A repeated posting pattern reveals when the home is usually empty."),
            listOf("Share holiday photos after returning rather than broadcasting travel in real time.", "Restrict travel updates to a carefully reviewed audience and avoid revealing exact dates or addresses.", "Disable unnecessary location tagging and check photo metadata before sharing publicly."),
            "Remove or restrict posts that reveal your current absence, review who can see your location, and ask a trusted person to check on the property if appropriate.",
            "Enjoy the trip first and post later. Keep live location and absence details away from public timelines."
        ),
        "art-5-8" to article(
            "Selling an Old Phone or Laptop? Erase It Securely First",
            "Deleting photos or performing a quick reset is not always enough preparation before handing a device to someone else.",
            "Modern phones often encrypt storage and provide a secure factory-reset process, while computers may retain recoverable data if drives are not properly erased. The correct method depends on the device, operating system, storage type, and whether encryption was enabled.",
            "Before selling a laptop, an owner deletes personal folders but leaves the user account and browser profile intact. The buyer later finds saved sessions and private files that were never properly removed.",
            listOf("The device is handed over with personal accounts still signed in or a removable memory card still installed.", "The owner deletes files manually but skips the manufacturer's reset or secure-erasure process.", "Backups, browser sessions, authenticator apps, or cloud accounts remain linked to the device."),
            listOf("Back up needed files, sign out of accounts, remove SIM and memory cards, and unlink the device from account dashboards.", "Use the manufacturer's supported factory-reset or secure-erase procedure; for computers, follow the operating system's drive-cleaning guidance.", "Verify that the device starts at its initial setup screen and that your accounts no longer list it as trusted."),
            "If a device was transferred with accounts still active, remotely sign out or revoke the device where possible, change exposed passwords, and review account sessions and recovery settings.",
            "Prepare devices for transfer with the official secure reset process, not just manual file deletion."
        ),
        "art-5-9" to article(
            "Convenient Social Login Can Grant More Access Than You Expect",
            "The 'Continue with Google' or 'Log in with Facebook' button can be useful, but the permissions screen deserves a careful read.",
            "Single sign-on can let a website identify you without giving it your password. However, the consent screen may authorize access to profile data, email, contacts, or other services. The risk depends on the exact permissions and the provider's controls.",
            "A user signs in to an unfamiliar game using a social account and approves permissions without reading them. Later, the user discovers the game still has access to profile information even though it is no longer used.",
            listOf("An unfamiliar site asks for permissions unrelated to its basic function.", "The consent screen includes posting, contact, file, or mailbox access that you did not expect.", "Old apps remain connected to your account long after you stop using them."),
            listOf("Check the publisher and requested permissions before approving social sign-in.", "Prefer the minimum permission set and deny access that is not essential to the service.", "Regularly review connected apps in the identity provider's security settings and revoke unused or suspicious access."),
            "Revoke the app's access from your Google, Apple, or social account settings, then inspect recent security activity. Change your password if you entered it directly into a suspicious page rather than using the official sign-in flow.",
            "Social login does not automatically share everything, but every permission matters. Approve only what the service genuinely needs."
        ),
        "art-5-10" to article(
            "A Practical Cleanup of Your Digital Footprint",
            "Your digital footprint includes old accounts, public posts, exposed contact details, app permissions, and information that search engines can find.",
            "Reducing your footprint means limiting unnecessary public information and closing access paths you no longer use. It cannot guarantee that every copy or archived record disappears, but it makes profiling and impersonation harder.",
            "A professional reviews old forum accounts, removes outdated public contact details, tightens social privacy settings, and revokes unused app access. Over time, less personal information is available to strangers.",
            listOf("Old accounts still contain your real name, email, phone number, or personal photos.", "Public search results expose home, work, family, or contact information you no longer want visible.", "Many unused apps and websites remain connected to your main accounts."),
            listOf("Search for your name and common usernames, then review results and request removal from the original site where appropriate.", "Delete or deactivate accounts you no longer need and tighten the privacy settings of accounts you keep.", "Revoke unused app permissions, remove unnecessary public profile details, and use unique passwords with multifactor authentication."),
            "Prioritize sensitive exposures such as identity documents, addresses, and account credentials. Contact the website or platform hosting the information and use its official removal process; monitor accounts if credentials were exposed.",
            "Digital-footprint cleanup is ongoing. Reduce public details, close unused access, and protect the accounts that remain."
        ),
        "art-6-1" to article(
            "Family and Children’s Photos: Why Public Sharing Is Risky",
            "This lesson explains family and children’s photos: why public sharing is risky and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving family and children’s photos: why public sharing is risky. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about family and children’s photos: why public sharing is risky: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-1" to article(
            "What Happens When You Join Free Café Wi-Fi?",
            "This lesson explains what happens when you join free café wi-fi? and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving what happens when you join free café wi-fi?. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about what happens when you join free café wi-fi?: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-1" to article(
            "One Key for Every Door: The Danger of Reused Passwords",
            "This lesson explains one key for every door: the danger of reused passwords and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving one key for every door: the danger of reused passwords. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about one key for every door: the danger of reused passwords: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-1" to article(
            "Anatomy of a Fake Job Advertisement",
            "This lesson explains anatomy of a fake job advertisement and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving anatomy of a fake job advertisement. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about anatomy of a fake job advertisement: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-1" to article(
            "“Congratulations! You Won a 5,000 EGP Voucher”",
            "This lesson explains “congratulations! you won a 5,000 egp voucher” and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving “congratulations! you won a 5,000 egp voucher”. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about “congratulations! you won a 5,000 egp voucher”: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-1" to article(
            "The Suspicious Call: A Polished Voice and a Script to Steal",
            "This lesson explains the suspicious call: a polished voice and a script to steal and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving the suspicious call: a polished voice and a script to steal. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about the suspicious call: a polished voice and a script to steal: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-1" to article(
            "Using Mobile Wallets Safely: Vodafone Cash, Orange, Etisalat, WE, and Banks",
            "This lesson explains using mobile wallets safely: vodafone cash, orange, etisalat, we, and banks and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving using mobile wallets safely: vodafone cash, orange, etisalat, we, and banks. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about using mobile wallets safely: vodafone cash, orange, etisalat, we, and banks: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-1" to article(
            "The Myth of Guaranteed 30% Monthly Returns: Ponzi Schemes",
            "This lesson explains the myth of guaranteed 30% monthly returns: ponzi schemes and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving the myth of guaranteed 30% monthly returns: ponzi schemes. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about the myth of guaranteed 30% monthly returns: ponzi schemes: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-2" to article(
            "Location Check-ins and Tracking: The Cost of Oversharing",
            "This lesson explains location check-ins and tracking: the cost of oversharing and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving location check-ins and tracking: the cost of oversharing. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about location check-ins and tracking: the cost of oversharing: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-2" to article(
            "Evil Twin Attacks: A Fake Copy of a Trusted Network",
            "This lesson explains evil twin attacks: a fake copy of a trusted network and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving evil twin attacks: a fake copy of a trusted network. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about evil twin attacks: a fake copy of a trusted network: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-2" to article(
            "Credential Stuffing: How Stolen Passwords Unlock Other Accounts",
            "This lesson explains credential stuffing: how stolen passwords unlock other accounts and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving credential stuffing: how stolen passwords unlock other accounts. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about credential stuffing: how stolen passwords unlock other accounts: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-2" to article(
            "Task Scams, Telegram Groups, and “Top-Up” Traps",
            "This lesson explains task scams, telegram groups, and “top-up” traps and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving task scams, telegram groups, and “top-up” traps. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about task scams, telegram groups, and “top-up” traps: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-2" to article(
            "The Share-to-Claim Trap: Forward This to 20 People",
            "This lesson explains the share-to-claim trap: forward this to 20 people and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving the share-to-claim trap: forward this to 20 people. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about the share-to-claim trap: forward this to 20 people: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-2" to article(
            "Fake Telecom and Internet Technical Support Calls",
            "This lesson explains fake telecom and internet technical support calls and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake telecom and internet technical support calls. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake telecom and internet technical support calls: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-2" to article(
            "Cardless ATM Cash-Out Scams",
            "This lesson explains cardless atm cash-out scams and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving cardless atm cash-out scams. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about cardless atm cash-out scams: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-2" to article(
            "Fake Crypto-Mining Platforms and Betting Apps",
            "This lesson explains fake crypto-mining platforms and betting apps and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake crypto-mining platforms and betting apps. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake crypto-mining platforms and betting apps: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-3" to article(
            "Unknown Friend Requests and Fake Profiles",
            "This lesson explains unknown friend requests and fake profiles and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving unknown friend requests and fake profiles. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about unknown friend requests and fake profiles: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-3" to article(
            "Man-in-the-Middle Attacks: How Network Traffic Is Intercepted",
            "This lesson explains man-in-the-middle attacks: how network traffic is intercepted and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving man-in-the-middle attacks: how network traffic is intercepted. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about man-in-the-middle attacks: how network traffic is intercepted: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-3" to article(
            "Data Breaches: How to Check Whether Your Information Leaked",
            "This lesson explains data breaches: how to check whether your information leaked and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving data breaches: how to check whether your information leaked. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about data breaches: how to check whether your information leaked: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-3" to article(
            "Suspicious Data-Entry and Transcription Jobs",
            "This lesson explains suspicious data-entry and transcription jobs and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving suspicious data-entry and transcription jobs. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about suspicious data-entry and transcription jobs: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-3" to article(
            "Trojan Apps Disguised as Prize and Giveaway Apps",
            "This lesson explains trojan apps disguised as prize and giveaway apps and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving trojan apps disguised as prize and giveaway apps. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about trojan apps disguised as prize and giveaway apps: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-3" to article(
            "Fake Government Benefits and Ration-Card Update Messages",
            "This lesson explains fake government benefits and ration-card update messages and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake government benefits and ration-card update messages. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake government benefits and ration-card update messages: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-3" to article(
            "“I Sent You 500 EGP by Mistake”: Fake Transfer Messages",
            "This lesson explains “i sent you 500 egp by mistake”: fake transfer messages and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving “i sent you 500 egp by mistake”: fake transfer messages. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about “i sent you 500 egp by mistake”: fake transfer messages: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-3" to article(
            "Luxury Lifestyle Displays Used to Recruit Investment Victims",
            "This lesson explains luxury lifestyle displays used to recruit investment victims and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving luxury lifestyle displays used to recruit investment victims. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about luxury lifestyle displays used to recruit investment victims: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-4" to article(
            "Two-Factor Authentication: An Essential Extra Lock",
            "This lesson explains two-factor authentication: an essential extra lock and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving two-factor authentication: an essential extra lock. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about two-factor authentication: an essential extra lock: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-4" to article(
            "Never Access Banking or Wallets over Public Wi-Fi",
            "This lesson explains never access banking or wallets over public wi-fi and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving never access banking or wallets over public wi-fi. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about never access banking or wallets over public wi-fi: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-4" to article(
            "What Makes a Password Strong and Memorable?",
            "This lesson explains what makes a password strong and memorable? and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving what makes a password strong and memorable?. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about what makes a password strong and memorable?: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-4" to article(
            "Money Mules: When a “Job” Turns You into a Money Launderer",
            "This lesson explains money mules: when a “job” turns you into a money launderer and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving money mules: when a “job” turns you into a money launderer. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about money mules: when a “job” turns you into a money launderer: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-4" to article(
            "Fake Surveys Designed to Collect Phone Numbers",
            "This lesson explains fake surveys designed to collect phone numbers and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake surveys designed to collect phone numbers. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake surveys designed to collect phone numbers: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-4" to article(
            "Caller ID Spoofing: When Your Screen Shows the Bank’s Name",
            "This lesson explains caller id spoofing: when your screen shows the bank’s name and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving caller id spoofing: when your screen shows the bank’s name. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about caller id spoofing: when your screen shows the bank’s name: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-4" to article(
            "Protecting Your Wallet PIN from Shoulder Surfing",
            "This lesson explains protecting your wallet pin from shoulder surfing and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving protecting your wallet pin from shoulder surfing. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about protecting your wallet pin from shoulder surfing: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-4" to article(
            "Fake App Profits and Withdrawals That Never Arrive",
            "This lesson explains fake app profits and withdrawals that never arrive and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake app profits and withdrawals that never arrive. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake app profits and withdrawals that never arrive: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-5" to article(
            "Suspicious Direct Messages and Malicious Video Links",
            "This lesson explains suspicious direct messages and malicious video links and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving suspicious direct messages and malicious video links. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about suspicious direct messages and malicious video links: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-5" to article(
            "HTTPS, Padlocks, and SSL Certificates: What They Actually Mean",
            "This lesson explains https, padlocks, and ssl certificates: what they actually mean and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving https, padlocks, and ssl certificates: what they actually mean. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about https, padlocks, and ssl certificates: what they actually mean: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-5" to article(
            "Passphrases: Easier to Remember, Harder to Guess",
            "This lesson explains passphrases: easier to remember, harder to guess and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving passphrases: easier to remember, harder to guess. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about passphrases: easier to remember, harder to guess: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-5" to article(
            "Fake Overseas Employment Contracts and Visa Offers",
            "This lesson explains fake overseas employment contracts and visa offers and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake overseas employment contracts and visa offers. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake overseas employment contracts and visa offers: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-5" to article(
            "Fake TV Competitions and Expensive Premium Numbers",
            "This lesson explains fake tv competitions and expensive premium numbers and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake tv competitions and expensive premium numbers. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake tv competitions and expensive premium numbers: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-5" to article(
            "Remote-Access Apps: The AnyDesk and TeamViewer Trap",
            "This lesson explains remote-access apps: the anydesk and teamviewer trap and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving remote-access apps: the anydesk and teamviewer trap. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about remote-access apps: the anydesk and teamviewer trap: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-5" to article(
            "Fake Transfer Confirmations and Unexpected Approval Requests",
            "This lesson explains fake transfer confirmations and unexpected approval requests and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake transfer confirmations and unexpected approval requests. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake transfer confirmations and unexpected approval requests: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-5" to article(
            "Fake Robot-Rental and Energy-Generation Investments",
            "This lesson explains fake robot-rental and energy-generation investments and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake robot-rental and energy-generation investments. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake robot-rental and energy-generation investments: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-6" to article(
            "Story Privacy and Close Friends Lists",
            "This lesson explains story privacy and close friends lists and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving story privacy and close friends lists. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about story privacy and close friends lists: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-6" to article(
            "VPN Protection: A Safer Tunnel on Public Networks",
            "This lesson explains vpn protection: a safer tunnel on public networks and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving vpn protection: a safer tunnel on public networks. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about vpn protection: a safer tunnel on public networks: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-6" to article(
            "Password Managers: Your Secure Digital Vault",
            "This lesson explains password managers: your secure digital vault and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving password managers: your secure digital vault. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about password managers: your secure digital vault: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-6" to article(
            "Verifying a Company’s Commercial Registration and Address in Egypt",
            "This lesson explains verifying a company’s commercial registration and address in egypt and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving verifying a company’s commercial registration and address in egypt. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about verifying a company’s commercial registration and address in egypt: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-6" to article(
            "Fake Influencer Giveaways and Prize Scams",
            "This lesson explains fake influencer giveaways and prize scams and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake influencer giveaways and prize scams. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake influencer giveaways and prize scams: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-6" to article(
            "Reading an OTP Aloud to a Caller Can Hand Over Your Account",
            "This lesson explains reading an otp aloud to a caller can hand over your account and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving reading an otp aloud to a caller can hand over your account. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about reading an otp aloud to a caller can hand over your account: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-6" to article(
            "QR Codes and Wallet Statements: What Not to Share",
            "This lesson explains qr codes and wallet statements: what not to share and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving qr codes and wallet statements: what not to share. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about qr codes and wallet statements: what not to share: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-6" to article(
            "Forged Licences and Fake UK Company Certificates",
            "This lesson explains forged licences and fake uk company certificates and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving forged licences and fake uk company certificates. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about forged licences and fake uk company certificates: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-7" to article(
            "Hide Your Email and Phone Number from Social Searches",
            "This lesson explains hide your email and phone number from social searches and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving hide your email and phone number from social searches. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about hide your email and phone number from social searches: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-7" to article(
            "Disable Automatic Connection to Open Wi-Fi Networks",
            "This lesson explains disable automatic connection to open wi-fi networks and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving disable automatic connection to open wi-fi networks. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about disable automatic connection to open wi-fi networks: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-7" to article(
            "Passkeys: Moving Beyond Traditional Passwords",
            "This lesson explains passkeys: moving beyond traditional passwords and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving passkeys: moving beyond traditional passwords. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about passkeys: moving beyond traditional passwords: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-7" to article(
            "Why Freelance Payments Should Stay on the Official Platform",
            "This lesson explains why freelance payments should stay on the official platform and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving why freelance payments should stay on the official platform. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about why freelance payments should stay on the official platform: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-7" to article(
            "Street Giveaways, Forms, and QR-Code Traps",
            "This lesson explains street giveaways, forms, and qr-code traps and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving street giveaways, forms, and qr-code traps. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about street giveaways, forms, and qr-code traps: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-7" to article(
            "Pressure and Threats: How to End a Scam Call Safely",
            "This lesson explains pressure and threats: how to end a scam call safely and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving pressure and threats: how to end a scam call safely. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about pressure and threats: how to end a scam call safely: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-7" to article(
            "SIM-Swap Attacks: How Criminals Hijack Your Number and Wallet",
            "This lesson explains sim-swap attacks: how criminals hijack your number and wallet and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving sim-swap attacks: how criminals hijack your number and wallet. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about sim-swap attacks: how criminals hijack your number and wallet: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-7" to article(
            "The “Once-in-a-Lifetime Opportunity” Pressure Tactic",
            "This lesson explains the “once-in-a-lifetime opportunity” pressure tactic and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving the “once-in-a-lifetime opportunity” pressure tactic. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about the “once-in-a-lifetime opportunity” pressure tactic: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-8" to article(
            "Account Blackmail: What to Do if Your Photos Leak",
            "This lesson explains account blackmail: what to do if your photos leak and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving account blackmail: what to do if your photos leak. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about account blackmail: what to do if your photos leak: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-8" to article(
            "Personal Hotspot Security: Your Router in Your Pocket",
            "This lesson explains personal hotspot security: your router in your pocket and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving personal hotspot security: your router in your pocket. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about personal hotspot security: your router in your pocket: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-8" to article(
            "Security Questions: Why Honest Answers Can Be Risky",
            "This lesson explains security questions: why honest answers can be risky and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving security questions: why honest answers can be risky. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about security questions: why honest answers can be risky: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-8" to article(
            "Red Flags in Online Job Interviews",
            "This lesson explains red flags in online job interviews and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving red flags in online job interviews. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about red flags in online job interviews: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-8" to article(
            "How to Distinguish Legitimate Promotions from Fake Contests",
            "This lesson explains how to distinguish legitimate promotions from fake contests and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving how to distinguish legitimate promotions from fake contests. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about how to distinguish legitimate promotions from fake contests: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-8" to article(
            "Fake Appliance Maintenance and Warranty Representatives",
            "This lesson explains fake appliance maintenance and warranty representatives and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake appliance maintenance and warranty representatives. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake appliance maintenance and warranty representatives: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-8" to article(
            "Buying and Selling through Wallets Safely",
            "This lesson explains buying and selling through wallets safely and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving buying and selling through wallets safely. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about buying and selling through wallets safely: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-8" to article(
            "Regulated Investment Channels and Egypt’s Financial Regulator",
            "This lesson explains regulated investment channels and egypt’s financial regulator and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving regulated investment channels and egypt’s financial regulator. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about regulated investment channels and egypt’s financial regulator: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-9" to article(
            "Protecting Telegram from Account Takeovers and Suspicious Channels",
            "This lesson explains protecting telegram from account takeovers and suspicious channels and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving protecting telegram from account takeovers and suspicious channels. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about protecting telegram from account takeovers and suspicious channels: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-9" to article(
            "Are Hotel and Airport Wi-Fi Networks Really Safe?",
            "This lesson explains are hotel and airport wi-fi networks really safe? and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving are hotel and airport wi-fi networks really safe?. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about are hotel and airport wi-fi networks really safe?: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-9" to article(
            "Your Response Plan When a Website Reports a Data Breach",
            "This lesson explains your response plan when a website reports a data breach and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving your response plan when a website reports a data breach. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about your response plan when a website reports a data breach: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-9" to article(
            "Overpayment Scams and Reversed Cheques",
            "This lesson explains overpayment scams and reversed cheques and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving overpayment scams and reversed cheques. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about overpayment scams and reversed cheques: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-9" to article(
            "Fake Charity Messages Offering Cash Assistance",
            "This lesson explains fake charity messages offering cash assistance and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving fake charity messages offering cash assistance. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about fake charity messages offering cash assistance: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-9" to article(
            "Reporting Numbers That Impersonate Banks and Authorities",
            "This lesson explains reporting numbers that impersonate banks and authorities and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving reporting numbers that impersonate banks and authorities. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about reporting numbers that impersonate banks and authorities: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-9" to article(
            "Set Daily Withdrawal and Transfer Limits",
            "This lesson explains set daily withdrawal and transfer limits and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving set daily withdrawal and transfer limits. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about set daily withdrawal and transfer limits: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-9" to article(
            "Recovering from Financial Loss and Rejecting Greedy Offers",
            "This lesson explains recovering from financial loss and rejecting greedy offers and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving recovering from financial loss and rejecting greedy offers. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about recovering from financial loss and rejecting greedy offers: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-6-10" to article(
            "Recovering Hacked Facebook and Instagram Accounts",
            "This lesson explains recovering hacked facebook and instagram accounts and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving recovering hacked facebook and instagram accounts. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about recovering hacked facebook and instagram accounts: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-7-10" to article(
            "Five Golden Rules for Safer Browsing Away from Home",
            "This lesson explains five golden rules for safer browsing away from home and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving five golden rules for safer browsing away from home. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about five golden rules for safer browsing away from home: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-8-10" to article(
            "Checking Password Security in Your Browser",
            "This lesson explains checking password security in your browser and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving checking password security in your browser. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about checking password security in your browser: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-9-10" to article(
            "Emergency Steps After a Recruitment Scam",
            "This lesson explains emergency steps after a recruitment scam and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving emergency steps after a recruitment scam. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about emergency steps after a recruitment scam: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-10-10" to article(
            "Cleaning Your Device and Accounts After a Fake Prize Link",
            "This lesson explains cleaning your device and accounts after a fake prize link and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving cleaning your device and accounts after a fake prize link. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about cleaning your device and accounts after a fake prize link: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-11-10" to article(
            "Helping Parents and Older Adults Resist Phone Scams",
            "This lesson explains helping parents and older adults resist phone scams and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving helping parents and older adults resist phone scams. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about helping parents and older adults resist phone scams: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-12-10" to article(
            "Emergency Response to an Unauthorized Wallet Transaction",
            "This lesson explains emergency response to an unauthorized wallet transaction and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving emergency response to an unauthorized wallet transaction. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about emergency response to an unauthorized wallet transaction: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        ),
        "art-13-10" to article(
            "Documents and Legal Steps for Reporting Investment Fraud",
            "This lesson explains documents and legal steps for reporting investment fraud and the practical privacy and security risks involved. Learn how criminals exploit this situation, what warning signs to look for, and how to protect yourself before money, identity information, or account access is lost.",
            "The risk arises when people act on unverified requests, expose sensitive information, or trust a service, person, device, or offer without checking it independently. Attackers may use impersonation, urgency, misleading interfaces, stolen data, or social pressure. A familiar logo, professional tone, HTTPS symbol, or attractive promise is not proof of legitimacy.",
            "A user encounters a situation involving documents and legal steps for reporting investment fraud. The request appears routine and asks for quick action. Before responding, the user pauses, checks the details through an independently verified channel, and refuses to share credentials, payment information, identity documents, or access until the request is confirmed.",
            listOf("The request creates urgency, fear, excitement, or pressure to act before you can verify it.", "It asks for sensitive information, an advance payment, an authentication code, unusual permissions, or access that is not necessary.", "The sender, website, account, company, device, or transaction cannot be independently verified through official channels."),
            listOf("Stop and verify the person, organization, website, and request using contact details you obtained independently.", "Share only the minimum information necessary; never disclose passwords, PINs, or one-time verification codes.", "Use official apps and websites, enable available security protections, keep evidence, and report suspicious activity to the relevant provider."),
            "If you have already shared information, installed an app, transferred money, or granted access, stop further interaction. Contact the relevant bank or service provider immediately, secure affected accounts from a trusted device, revoke unknown sessions or permissions, preserve screenshots and transaction records, and report the incident through official channels.",
            "Stay cautious about documents and legal steps for reporting investment fraud: pause, verify independently, limit what you share, and act quickly through official channels if something goes wrong."
        )
    )

private val QUESTIONS = mapOf(
        "q-1-1" to AcademyTranslations.TranslatedQuestion(
            questionText = "You receive an SMS from an unknown number saying: ‘Your bank account has been suspended. Click here to update your details.’ What should you do?",
            situation = null,
            options = listOf(
                "Click immediately and enter my details so the account is not disabled.",
                "Call the number that sent the message and ask who they are.",
                "Ignore the message and call the bank using the official number printed on the back of my card.",
                "Forward the message to friends to ask what they think."
            ),
            explanation = "Banks do not ask customers to update sensitive information through links in unsolicited SMS messages. Contact your bank only through its official channels.",
            warningLesson = "Never use contact details or links supplied in a suspicious message. Find the bank’s official number independently."
        ),
        "q-1-2" to AcademyTranslations.TranslatedQuestion(
            questionText = "Phishing mainly relies on making victims feel afraid or greedy so they act quickly without thinking.",
            situation = null,
            options = listOf("True", "False"),
            explanation = "True. Scammers create a false emergency or promise an imaginary reward to disrupt clear thinking and pressure people into acting quickly.",
            warningLesson = "Pause whenever a message creates urgency or promises an unrealistic reward. Verify it independently."
        ),
        "q-1-3" to AcademyTranslations.TranslatedQuestion(
            questionText = "Why does a fake-delivery message ask for a very small fee, such as EGP 15 or 20?",
            situation = null,
            options = listOf(
                "Because the scammer is being generous and asking for very little.",
                "To lure you into entering your card details and OTP so the attacker can steal your money.",
                "To cover the cost of sending the SMS.",
                "Because the parcel will genuinely be delivered."
            ),
            explanation = "The small amount is bait. The real goal is to make you open a fake payment page and disclose your full card details and one-time passcode.",
            warningLesson = null
        ),
        "q-1-4" to AcademyTranslations.TranslatedQuestion(
            questionText = "Official couriers and postal services always ask customers to enter card details through random SMS links to receive parcels.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "False. Do not trust random payment links in text messages. Verify fees and tracking through the courier’s official website, app, or verified customer service.",
            warningLesson = null
        ),
        "q-1-5" to AcademyTranslations.TranslatedQuestion(
            questionText = "A close friend messages you on WhatsApp: ‘I entered your number by mistake. Send me the SMS code urgently.’ What should you do?",
            situation = null,
            options = listOf(
                "Send the code immediately so your friend is not delayed.",
                "Refuse and call your friend directly; their account may be compromised.",
                "Post the code on Facebook.",
                "Delete WhatsApp from your phone."
            ),
            explanation = "The code is for activating your own account. Sharing it may let an attacker take over your WhatsApp. Verify the request by calling your friend.",
            warningLesson = null
        ),
        "q-1-6" to AcademyTranslations.TranslatedQuestion(
            questionText = "A six-digit WhatsApp verification code may be shared with close family members only.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "False. Verification codes are private and must never be shared with anyone, including family and friends.",
            warningLesson = null
        ),
        "q-1-7" to AcademyTranslations.TranslatedQuestion(
            questionText = "Which of the following is the genuine Google sign-in domain?",
            situation = null,
            options = listOf(
                "google.com.security-verify.xyz",
                "login-google.com.site",
                "accounts.google.com/signin",
                "google-login-egypt.net"
            ),
            explanation = "accounts.google.com is a subdomain of google.com. The other examples are separate domains that merely include Google’s name.",
            warningLesson = null
        ),
        "q-1-8" to AcademyTranslations.TranslatedQuestion(
            questionText = "If the word ‘facebook’ appears at the beginning of a link, the link must belong to Facebook.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "False. A scammer can place a brand name in a subdomain or elsewhere in a URL. Check the registered domain, not the familiar word.",
            warningLesson = null
        ),
        "q-1-9" to AcademyTranslations.TranslatedQuestion(
            questionText = "An unknown international number offers you EGP 2,000 per day to rate videos from home. What is the most likely explanation?",
            situation = null,
            options = listOf(
                "A great opportunity to earn money quickly.",
                "A common task scam that may offer a small initial reward before demanding large deposits.",
                "A genuine job officially run by YouTube.",
                "A licensed government job."
            ),
            explanation = "Task scams may pay a small amount at first to build trust, then pressure victims to deposit larger sums to unlock tasks or withdraw supposed earnings.",
            warningLesson = null
        ),
        "q-1-10" to AcademyTranslations.TranslatedQuestion(
            questionText = "If a recruiter asks you to pay an ‘insurance fee’ or deposit money into a wallet before you can start a job, it is a major scam warning sign.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "True. A legitimate employer should not require you to pay money to receive work or unlock tasks. Verify the employer independently.",
            warningLesson = null
        ),
        "q-1-11" to AcademyTranslations.TranslatedQuestion(
            questionText = "Someone claiming to be a bank employee asks for the three-digit CVV on the back of your card. What should you do?",
            situation = null,
            options = listOf(
                "Read the numbers quickly so the card is not blocked.",
                "Refuse and end the call; bank staff should never ask you to disclose your CVV.",
                "Give the card number but hide only the CVV.",
                "Ask the caller to call back in an hour."
            ),
            explanation = "Your CVV, PIN, passwords, and OTPs are confidential. Anyone asking you to disclose them may be impersonating your bank.",
            warningLesson = null
        ),
        "q-1-12" to AcademyTranslations.TranslatedQuestion(
            questionText = "A message claiming your bank account is suspended comes from an ordinary personal mobile number. This is a strong warning sign.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "True. An unexpected banking alert from an unknown personal number should be treated as suspicious. Verify it through the bank’s official channels.",
            warningLesson = null
        ),
        "q-1-13" to AcademyTranslations.TranslatedQuestion(
            questionText = "You see an urgent charity appeal on Facebook with an external payment link. What is the safest way to donate?",
            situation = null,
            options = listOf(
                "Open the link and enter your card details immediately.",
                "Transfer money to any wallet number in the post.",
                "Donate through a verified, officially registered charity and its known payment channels.",
                "Share the post with friends to collect donations."
            ),
            explanation = "Donate through verified organizations and independently confirmed payment channels. This helps ensure the money reaches the intended recipients and protects your payment details.",
            warningLesson = null
        ),
        "q-1-14" to AcademyTranslations.TranslatedQuestion(
            questionText = "Scammers can use AI tools to create fake images or videos of sick children to solicit fraudulent donations.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "True. Generative AI can create convincing but fabricated emotional content. Verify the appeal and organization before donating.",
            warningLesson = null
        ),
        "q-1-15" to AcademyTranslations.TranslatedQuestion(
            questionText = "A social media post promises 50 GB of free mobile data if you dial a specific code. What could happen?",
            situation = null,
            options = listOf(
                "You will receive a large free data bundle.",
                "Your balance may be transferred or you may be subscribed to paid services.",
                "Your phone will become 200% faster.",
                "Nothing can happen."
            ),
            explanation = "Unknown USSD codes may transfer credit or activate paid subscriptions. Check any offer with your mobile provider first.",
            warningLesson = null
        ),
        "q-1-16" to AcademyTranslations.TranslatedQuestion(
            questionText = "Mobile operators publish legitimate offers through their official apps and verified channels, not through unverified social media rumors.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "True. Confirm offers in your carrier’s official app, website, or verified customer support channels.",
            warningLesson = null
        ),
        "q-1-17" to AcademyTranslations.TranslatedQuestion(
            questionText = "A hotel-booking message asks you to pay through an email link instead of the official booking app. What should you do?",
            situation = null,
            options = listOf(
                "Pay through the link to avoid cancellation.",
                "Ignore the link and manage the reservation and payment through the official platform.",
                "Email a photo of your bank card.",
                "Cancel the entire trip immediately."
            ),
            explanation = "Use the official booking platform and verify the request directly with the property through trusted contact details. External payment links may be phishing attempts.",
            warningLesson = null
        ),
        "q-1-18" to AcademyTranslations.TranslatedQuestion(
            questionText = "If a hotel asks you to transfer money to a private account outside the booking platform, the platform guarantees that you will get your money back if it is a scam.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "False. Payments made outside a platform’s protected system may not be covered by its protections. Keep payments within the official platform.",
            warningLesson = null
        ),
        "q-1-19" to AcademyTranslations.TranslatedQuestion(
            questionText = "Which number is the dedicated cybercrime hotline in Egypt for reporting information technology crimes?",
            situation = null,
            options = listOf(
                "122",
                "123",
                "108",
                "180"
            ),
            explanation = "The lesson identifies 108 as the hotline for reporting cybercrime in Egypt. Check official government sources for the latest reporting details.",
            warningLesson = null
        ),
        "q-1-20" to AcademyTranslations.TranslatedQuestion(
            questionText = "Reporting phishing messages and blocking suspicious accounts can help protect other users.",
            situation = null,
            options = listOf(
                "True",
                "False"
            ),
            explanation = "True. Reports help platforms and relevant authorities identify malicious accounts and links, limit their reach, and protect other people.",
            warningLesson = null
        ),
    )
}

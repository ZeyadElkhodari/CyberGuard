package com.example.data

import com.example.model.*
import com.example.ui.theme.*

object SecurityData {
    val initialUserStats = UserStats(
        score = 0,
        threatsBlocked = 0,
        scannedContentCount = 0,
        scansDone = 0,
        level = 1,
        xp = 0,
        xpToNextLevel = 100,
        rankTitle = "Security Explorer"
    )

    val developersList = listOf(
        Developer("Eng / Zeyad Yasser Elkhodari"),
        Developer("Eng / Saja Aymen Elkhodari"),
        Developer("Eng / Haneen mahmoud ebrahim"),
        Developer("Eng / Shahd Adel aboelnaga"),
        Developer("Eng / Ranim Hany Mousa"),
        Developer("Eng / Donia Khaled Younis")
    )

    val initialAlerts = emptyList<SecurityAlert>()

    val initialCourses = listOf(
        Course(
            id = "course-1",
            title = "Ethical Hacking Basics",
            category = "Cybersecurity • 8 lessons",
            lessonsCount = 8,
            progress = 0f,
            color = CyberRed,
            iconName = "Shield",
            description = "Learn the principles of ethical penetration testing, vulnerability discovery, and defense-in-depth methodologies.",
            lessons = listOf(
                Lesson(
                    id = "c1-l1",
                    title = "Introduction to Ethical Hacking & CIA Triad",
                    duration = "6 min",
                    completed = false,
                    content = "Ethical hacking involves identifying weaknesses in computers and network systems by using malicious hacker tools and tactics, but with permission and legal authorization to improve security. The CIA triad (Confidentiality, Integrity, Availability) represents the foundational pillars of information security.",
                    quizQuestion = QuizQuestion(
                        question = "What does the \"I\" stand for in the CIA triad of cybersecurity?",
                        options = listOf("Identification", "Integrity", "Inspection", "Isolation"),
                        correctIndex = 1,
                        explanation = "Integrity guarantees that data has not been altered, modified, or tampered with by unauthorized parties."
                    )
                ),
                Lesson(
                    id = "c1-l2",
                    title = "Reconnaissance & Footprinting Techniques",
                    duration = "10 min",
                    completed = false,
                    content = "Reconnaissance is the preparatory phase where an ethical hacker gathers as much information as possible about a target system before launching an assessment. Passive recon uses public records (OSINT, DNS records, WHOIS), while active recon directly probes the target (port scanning, banner grabbing).",
                    quizQuestion = QuizQuestion(
                        question = "Which of the following is considered a passive reconnaissance method?",
                        options = listOf(
                            "Active port scan via Nmap",
                            "Inspecting public DNS and WHOIS records",
                            "Sending SYN flood packets",
                            "Exploiting a buffer overflow"
                        ),
                        correctIndex = 1,
                        explanation = "Passive reconnaissance gathers information without directly transmitting packets to the target server."
                    )
                ),
                Lesson(
                    id = "c1-l3",
                    title = "Vulnerability Scanning & CVE Analysis",
                    duration = "12 min",
                    completed = false,
                    content = "CVE (Common Vulnerabilities and Exposures) is a standardized list of publicly known cyber security flaws. Vulnerability scanners compare installed software versions against CVE databases to flag unpatched exploits.",
                    quizQuestion = QuizQuestion(
                        question = "What does CVE stand for in vulnerability management?",
                        options = listOf(
                            "Central Virtual Endpoint",
                            "Common Vulnerabilities and Exposures",
                            "Cyber Vector Exploitation",
                            "Certified Verified Entry"
                        ),
                        correctIndex = 1,
                        explanation = "CVE stands for Common Vulnerabilities and Exposures, managed by MITRE and NIST."
                    )
                )
            )
        ),
        Course(
            id = "course-2",
            title = "Network Security",
            category = "Networking • 12 lessons",
            lessonsCount = 12,
            progress = 0f,
            color = CyberBlue,
            iconName = "Network",
            description = "Master packet analysis, firewalls, TLS/SSL handshake encryption, and Wi-Fi perimeter protections.",
            lessons = listOf(
                Lesson(
                    id = "c2-l1",
                    title = "Understanding Firewalls & Packet Inspection",
                    duration = "8 min",
                    completed = false,
                    content = "Firewalls act as network traffic gatekeepers. Stateful inspection tracks the state of active network connections to determine whether incoming packets belong to an existing legitimate session.",
                    quizQuestion = QuizQuestion(
                        question = "Which layer of the OSI model does a traditional packet filtering firewall operate at?",
                        options = listOf(
                            "Layer 3 (Network Layer)",
                            "Layer 7 (Application)",
                            "Layer 1 (Physical)",
                            "Layer 6 (Presentation)"
                        ),
                        correctIndex = 0,
                        explanation = "Standard packet filtering examines IP addresses and ports at Layer 3 (Network) and Layer 4 (Transport)."
                    )
                ),
                Lesson(
                    id = "c2-l2",
                    title = "TLS/HTTPS Handshake & Certificate Authorities",
                    duration = "11 min",
                    completed = false,
                    content = "Transport Layer Security (TLS) encrypts web traffic between browsers and servers. It relies on asymmetric cryptography for key exchange and symmetric encryption for fast data transit.",
                    quizQuestion = QuizQuestion(
                        question = "What is the primary role of a Certificate Authority (CA)?",
                        options = listOf(
                            "To store user passwords in plain text",
                            "To digitally sign and validate website identity certificates",
                            "To block all incoming VPN traffic",
                            "To speed up DNS caching queries"
                        ),
                        correctIndex = 1,
                        explanation = "CAs vouch for domain identity by validating and signing cryptographic certificates."
                    )
                )
            )
        ),
        Course(
            id = "course-3",
            title = "Linux Essentials for Security",
            category = "Linux • 10 lessons",
            lessonsCount = 10,
            progress = 0f,
            color = CyberGreen,
            iconName = "Terminal",
            description = "Essential command-line skills, file permission models (chmod, chown), process monitoring, and hardening SSH configs.",
            lessons = listOf(
                Lesson(
                    id = "c3-l1",
                    title = "File Permissions & Octal Notation (chmod)",
                    duration = "7 min",
                    completed = false,
                    content = "Linux uses Read (4), Write (2), and Execute (1) permissions across User, Group, and Others. For example, chmod 755 grants rwxr-xr-x.",
                    quizQuestion = QuizQuestion(
                        question = "What permission does the number 6 represent in Linux octal notation?",
                        options = listOf(
                            "Read only (4)",
                            "Read and Write (4 + 2)",
                            "Execute and Write (1 + 2)",
                            "Full control (7)"
                        ),
                        correctIndex = 1,
                        explanation = "4 (Read) + 2 (Write) = 6 (rw-)."
                    )
                )
            )
        ),
        Course(
            id = "course-4",
            title = "Web Application Security",
            category = "Web Security • 9 lessons",
            lessonsCount = 9,
            progress = 0f,
            color = CyberPurple,
            iconName = "Globe",
            description = "OWASP Top 10 vulnerabilities: SQL Injection, Cross-Site Scripting (XSS), CSRF, and Broken Access Control.",
            lessons = listOf(
                Lesson(
                    id = "c4-l1",
                    title = "SQL Injection: Causes and Prepared Statements",
                    duration = "9 min",
                    completed = false,
                    content = "SQL injection occurs when untrusted user input is directly concatenated into a dynamic SQL database query. The gold standard prevention is using parameterized queries (Prepared Statements).",
                    quizQuestion = QuizQuestion(
                        question = "What is the most effective defense against SQL Injection?",
                        options = listOf(
                            "Encoding input with Base64",
                            "Using Parameterized Queries (Prepared Statements)",
                            "Shortening input field length to 10 chars",
                            "Hiding the database error messages only"
                        ),
                        correctIndex = 1,
                        explanation = "Parameterized queries ensure the database treats input strictly as data, never as executable code."
                    )
                )
            )
        )
    )

    val initialWifiChecks = listOf(
        WifiCheckItem(
            id = 1,
            titleEn = "WPA3 / WPA2-AES Encryption Active",
            titleAr = "تشفير WPA3 أو WPA2-AES مفعل",
            descEn = "Avoid deprecated WEP or Open networks which transmit data in cleartext.",
            descAr = "تجنب شبكات WEP القديمة أو المفتوحة التي تبث البيانات دون تشفير.",
            checked = false
        ),
        WifiCheckItem(
            id = 2,
            titleEn = "Default Admin Password Replaced",
            titleAr = "تغيير كلمة مرور لوحة تحكم الراوتر",
            descEn = "Factory defaults (admin/admin or 1234) can be breached by simple bots.",
            descAr = "الافتراضية مثل admin/admin يسهل اختراقها بهجمات المعاجم الآلية.",
            checked = false
        ),
        WifiCheckItem(
            id = 3,
            titleEn = "Remote Management Disabled",
            titleAr = "تعطيل الإدارة عن بعد (WAN Remote Admin)",
            descEn = "Do not allow router admin portal access from the public internet.",
            descAr = "منع الوصول للوحة تحكم الراوتر من خارج الشبكة المنزلية عبر الإنترنت.",
            checked = false
        ),
        WifiCheckItem(
            id = 4,
            titleEn = "Isolated Guest Network for Smart IoT Devices",
            titleAr = "شبكة ضيوف معزولة لأجهزة إنترنت الأشياء",
            descEn = "Keeps compromised smart bulbs or cameras isolated from personal laptops.",
            descAr = "عزل الكاميرات الذكية والأجهزة المنزلية عن أجهزتك الشخصية وحساباتك البنكية.",
            checked = false
        ),
        WifiCheckItem(
            id = 5,
            titleEn = "Encrypted DNS (DoH / DoT / Cloudflare / Quad9)",
            titleAr = "تفعيل استعلامات DNS المشفرة (DoH / DoT)",
            descEn = "Prevents ISPs and local snoopers from viewing your domain lookups.",
            descAr = "حماية خصوصية أسماء المواقع التي تتصفحها من مراقبة مزود الخدمة.",
            checked = false
        )
    )
}

typealias MockSecurityData = SecurityData

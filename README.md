**HealthBridge** is a Spring Boot backend that lets Indian families upload medical lab reports (blood tests, liver function, thyroid etc.) and get back a plain-language explanation — in Telugu, Hindi, or English . Along with Indian diet recommendations based on the results.
-> It's not just for one person.
-> You can add your entire family — father, mother, spouse  and track everyone's health under one account.
-> The system also monitors trends across multiple reports and flags when something like Haemoglobin or B12 has been consistently low.

**The Problem With Existing Apps**
-> I looked at apps like NirogGyan, BloodGPT, Docus AI before building this. They all do the same thing , explain blood test values in English to an English-speaking audience only.

**What's missing in already existing apps**
No regional language support
Built for individuals
Generic western diet advice
One-time analysis only
English medical jargon

**What HealthBridge does**
Explains reports in Telugu and Hindi
One account manages the whole family
Tracks trends across months, sends alerts
Simple language anyone can understand

**Features**
**🔐 Auth**

User registration with BCrypt password encryption
JWT login — token based authentication on all routes

**👨‍👩‍👧 Family Management**

Add multiple family members under one account
Each member has their own report history

**📄 Report Upload + AI Analysis**

Upload any PDF from Dr Lal, Thyrocare, Apollo, or any lab
PDFBox extracts the raw text
LLM explains every biomarker in simple language
Flags which values are normal and which are abnormal
Suggests 3 questions to ask your doctor

**🗣️ Language Support**

Set your preferred language during registration
All AI explanations come back in Telugu, Hindi, or English
Diet recommendations are also in your language

**🥗 Indian Diet Recommendations**

Low iron? → "Eat rajma, palak, til"
Low Vitamin D? → "Spend time in sunlight, have ragi and drumstick leaves"
High cholesterol? → "Avoid ghee excess, have oats, methi seeds"
Not "eat kale" , actual Indian household food

**📈 Trend Monitoring**

Upload reports over time , the system compares them
Detects if Haemoglobin, B12, cholesterol are improving or worsening
Spring Scheduler runs every Monday morning automatically

**📚 Swagger UI**

Full interactive API docs at /swagger-ui.html
Test every endpoint directly from the browser

**Sample Flow**
Register with Telugu as preferred language:
jsonPOST /api/auth/register
{
  "fullName": "Ravi Kumar",
  "email": "ravi@gmail.com",
  "password": "ravi1234",
  "phone": "9876543210",
  "preferredLanguage": "TELUGU"
}
Add father as a family member:
jsonPOST /api/family/add
{
  "fullName": "Ravi's Father",
  "relationship": "Father",
  "age": 58,
  "gender": "Male"
}
Upload his blood test PDF:
POST /api/reports/upload/1
Content-Type: multipart/form-data

labName: Dr Lal Pathlabs
reportType: Blood Test
file: blood_report.pdf

What you get back:

The aiExplanation field comes back in Telugu — explaining every value,
flagging abnormal ones, and suggesting what to ask the doctor.
The dietRecommendations field gives specific Indian food advice based on the results.

**Sample AI Response (Telugu)**
నమస్తే రవి కుమార్ గారు,

అసాధారణ విలువలు:
- హీమోగ్లోబిన్ 10.2 g/dL — తక్కువగా ఉంది (సాధారణ: 13-17)
- విటమిన్ B12 185 pg/mL — తక్కువగా ఉంది
- HbA1c 6.2% — ప్రీ-డయాబెటిక్ పరిధిలో ఉంది
- TSH 4.8 — బోర్డర్‌లైన్ హైపోథైరాయిడిజం

ఆహార సూచనలు:
- ఇనుము పెంచడానికి: రాజ్మా, పాలక్, తిల్ తినండి
- విటమిన్ B12 కోసం: పాలు, పన్నీర్, గుడ్లు తినండి
- షుగర్ నియంత్రణకు: మెంతి, వెంద్రైకాయ తినండి

దయచేసి మీ డాక్టర్‌తో మాట్లాడండి.

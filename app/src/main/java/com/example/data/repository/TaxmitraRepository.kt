package com.example.data.repository

import com.example.data.local.ScannedTaxDocumentDao
import com.example.data.local.TaxmitraDao
import com.example.data.model.BusinessEntity
import com.example.data.model.CaEngagementEntity
import com.example.data.model.CaProfile
import com.example.data.model.ComplianceTaskEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.ScannedTaxDocumentEntity
import com.example.data.model.TaxNoticeEntity
import com.example.data.model.TaxProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull

class TaxmitraRepository(
    private val dao: TaxmitraDao,
    private val scannedTaxDao: ScannedTaxDocumentDao? = null
) {

    // Pre-populated verified Chartered Accountants
    val verifiedCas: List<CaProfile> = listOf(
        CaProfile(
            id = "ca_101",
            name = "CA Rajesh Sharma, FCA",
            icaiNumber = "ICAI #054821",
            firmName = "Sharma & Associates",
            experienceYears = 14,
            city = "New Delhi",
            rating = 4.9f,
            reviewCount = 184,
            isVerified = true,
            specializations = listOf("ITR-1 to 4", "Tax Audit u/s 44AB", "Tax Notice Resolution"),
            languages = listOf("English", "Hindi"),
            startingFee = 1499.0,
            bio = "Senior fellow CA specializing in individual tax planning, expatriate returns, and Section 143/148 CPC notice resolution."
        ),
        CaProfile(
            id = "ca_102",
            name = "CA Ananya Deshmukh, ACA",
            icaiNumber = "ICAI #089243",
            firmName = "VentureTax Advisors",
            experienceYears = 8,
            city = "Mumbai / Bengaluru",
            rating = 4.95f,
            reviewCount = 210,
            isVerified = true,
            specializations = listOf("Pvt Ltd & LLP Formation", "Startup India DPIIT", "GST Filing"),
            languages = listOf("English", "Hindi", "Marathi"),
            startingFee = 1999.0,
            bio = "Ex-Big4 consultant helping digital startups, D2C brands, and tech founders with corporate compliance and fundraising tax structure."
        ),
        CaProfile(
            id = "ca_103",
            name = "CA Vikram Singhal, FCA",
            icaiNumber = "ICAI #042119",
            firmName = "Singhal & Co.",
            experienceYears = 19,
            city = "Jaipur / Ahmedabad",
            rating = 4.85f,
            reviewCount = 142,
            isVerified = true,
            specializations = listOf("MSME Udyam", "GST Compliance", "Presumptive Tax 44AD"),
            languages = listOf("English", "Hindi", "Gujarati"),
            startingFee = 1299.0,
            bio = "Specialist in manufacturing, trading, and retail business compliance, GST reconciliation, and e-invoicing workflows."
        ),
        CaProfile(
            id = "ca_104",
            name = "CA Priya Swaminathan, ACA",
            icaiNumber = "ICAI #098712",
            firmName = "Apex Corporate Advisory",
            experienceYears = 6,
            city = "Chennai / Hyderabad",
            rating = 4.88f,
            reviewCount = 96,
            isVerified = true,
            specializations = listOf("Freelancer 44ADA", "Capital Gains", "Foreign Assets"),
            languages = listOf("English", "Tamil", "Telugu"),
            startingFee = 1599.0,
            bio = "Expert in software consulting income, RSUs/ESOPs taxation, international freelance remittance, and Form 67."
        )
    )

    // Business
    val allBusinesses: Flow<List<BusinessEntity>> = dao.getAllBusinesses()
    val primaryBusiness: Flow<BusinessEntity?> = dao.getPrimaryBusiness()

    suspend fun saveBusiness(business: BusinessEntity): Long {
        return if (business.id == 0L) {
            dao.insertBusiness(business)
        } else {
            dao.updateBusiness(business)
            business.id
        }
    }

    // Tax Profile
    val latestTaxProfile: Flow<TaxProfileEntity?> = dao.getLatestTaxProfile()

    suspend fun saveTaxProfile(profile: TaxProfileEntity): Long {
        return if (profile.id == 0L) {
            dao.insertTaxProfile(profile)
        } else {
            dao.updateTaxProfile(profile)
            profile.id
        }
    }

    // Documents
    val allDocuments: Flow<List<DocumentEntity>> = dao.getAllDocuments()
    fun getDocumentsByCategory(category: String): Flow<List<DocumentEntity>> = dao.getDocumentsByCategory(category)

    suspend fun saveDocument(document: DocumentEntity): Long = dao.insertDocument(document)
    suspend fun deleteDocument(id: Long) = dao.deleteDocument(id)

    // Scanned Tax Documents History (Room Database)
    val allScannedDocuments: Flow<List<ScannedTaxDocumentEntity>> = scannedTaxDao?.getAllScannedDocuments() ?: emptyFlow()
    fun getScannedDocumentsByType(docType: String): Flow<List<ScannedTaxDocumentEntity>> = 
        scannedTaxDao?.getScannedDocumentsByType(docType) ?: emptyFlow()
    fun getScannedDocumentsByCategory(category: String): Flow<List<ScannedTaxDocumentEntity>> = 
        scannedTaxDao?.getScannedDocumentsByCategory(category) ?: emptyFlow()

    suspend fun saveScannedTaxDocument(document: ScannedTaxDocumentEntity): Long {
        return scannedTaxDao?.insertScannedDocument(document) ?: 0L
    }

    suspend fun deleteScannedTaxDocument(id: Long) {
        scannedTaxDao?.deleteScannedDocumentById(id)
    }

    // Invoices
    val allInvoices: Flow<List<InvoiceEntity>> = dao.getAllInvoices()
    suspend fun saveInvoice(invoice: InvoiceEntity): Long {
        return if (invoice.id == 0L) {
            dao.insertInvoice(invoice)
        } else {
            dao.updateInvoice(invoice)
            invoice.id
        }
    }

    // Compliance Tasks
    val complianceTasks: Flow<List<ComplianceTaskEntity>> = dao.getAllComplianceTasks()
    suspend fun updateComplianceTask(task: ComplianceTaskEntity) = dao.updateComplianceTask(task)

    // CA Engagements
    val caEngagements: Flow<List<CaEngagementEntity>> = dao.getAllCaEngagements()
    suspend fun createCaEngagement(engagement: CaEngagementEntity): Long = dao.insertCaEngagement(engagement)

    // Tax Notices
    val taxNotices: Flow<List<TaxNoticeEntity>> = dao.getAllTaxNotices()
    suspend fun saveTaxNotice(notice: TaxNoticeEntity): Long {
        return if (notice.id == 0L) {
            dao.insertTaxNotice(notice)
        } else {
            dao.updateTaxNotice(notice)
            notice.id
        }
    }

    // Pre-populate realistic seed data on initial app launch
    suspend fun seedInitialDataIfNeeded() {
        val existingTasks = dao.getAllComplianceTasks().firstOrNull()
        if (existingTasks.isNullOrEmpty()) {
            val defaultTasks = listOf(
                ComplianceTaskEntity(
                    title = "ITR Filing (Individuals / Non-Audit)",
                    category = "INCOME_TAX",
                    dueDate = "July 31, 2025",
                    dueTimestamp = System.currentTimeMillis() + 86400000L * 45,
                    frequency = "Annual",
                    penaltyWarning = "Late fee of ₹5,000 u/s 234F + 1% per month interest u/s 234A",
                    authority = "Income Tax Department (e-Filing 2.0)",
                    officialUrl = "https://eportal.incometax.gov.in"
                ),
                ComplianceTaskEntity(
                    title = "Advance Tax (2nd Installment - 45%)",
                    category = "INCOME_TAX",
                    dueDate = "September 15, 2025",
                    dueTimestamp = System.currentTimeMillis() + 86400000L * 90,
                    frequency = "Quarterly",
                    penaltyWarning = "1% interest per month u/s 234C on shortfall",
                    authority = "Income Tax Department",
                    officialUrl = "https://eportal.incometax.gov.in"
                ),
                ComplianceTaskEntity(
                    title = "GSTR-1 Monthly Return (Outward Supplies)",
                    category = "GST",
                    dueDate = "11th of Every Month",
                    dueTimestamp = System.currentTimeMillis() + 86400000L * 12,
                    frequency = "Monthly",
                    penaltyWarning = "₹50/day late fee (₹20 for Nil return)",
                    authority = "GSTN Portal",
                    officialUrl = "https://www.gst.gov.in"
                ),
                ComplianceTaskEntity(
                    title = "GSTR-3B Monthly Summary & Tax Payment",
                    category = "GST",
                    dueDate = "20th of Every Month",
                    dueTimestamp = System.currentTimeMillis() + 86400000L * 21,
                    frequency = "Monthly",
                    penaltyWarning = "18% p.a. interest on unpaid tax + late filing fee",
                    authority = "GSTN Portal",
                    officialUrl = "https://www.gst.gov.in"
                ),
                ComplianceTaskEntity(
                    title = "Udyam MSME Annual Details Update",
                    category = "MSME",
                    dueDate = "March 31, 2026",
                    dueTimestamp = System.currentTimeMillis() + 86400000L * 200,
                    frequency = "Annual",
                    penaltyWarning = "Suspension of MSME priority benefits",
                    authority = "Ministry of MSME, Govt of India",
                    officialUrl = "https://udyamregistration.gov.in"
                ),
                ComplianceTaskEntity(
                    title = "ROC Annual Return (AOC-4 & MGT-7 for Companies)",
                    category = "ROC",
                    dueDate = "October 30, 2025",
                    dueTimestamp = System.currentTimeMillis() + 86400000L * 140,
                    frequency = "Annual",
                    penaltyWarning = "₹100/day per form with no upper ceiling",
                    authority = "Ministry of Corporate Affairs (MCA V3)",
                    officialUrl = "https://www.mca.gov.in"
                )
            )
            dao.insertComplianceTasks(defaultTasks)
        }

        // Default initial documents in vault
        val existingDocs = dao.getAllDocuments().firstOrNull()
        if (existingDocs.isNullOrEmpty()) {
            dao.insertDocument(
                DocumentEntity(
                    title = "Form 16 Part A & B (FY 2024-25)",
                    category = "TAX",
                    docType = "FORM_16",
                    ocrConfidence = 98,
                    extractedFieldsJson = "Employer: Infosys Ltd | TAN: BLRI01928A | Gross Salary: ₹12,40,000 | TDS: ₹72,400 | Sec 80C: ₹1,50,000",
                    status = "VERIFIED"
                )
            )
            dao.insertDocument(
                DocumentEntity(
                    title = "PAN Card (Permanent Account Number)",
                    category = "PERSONAL",
                    docType = "PAN",
                    ocrConfidence = 99,
                    extractedFieldsJson = "PAN: ABCDE1234F | Name: ANKIT PAL | DOB: 14/08/1996 | Father's Name: SURESH PAL",
                    status = "VERIFIED"
                )
            )
        }

        // Seed default initial scanned tax documents history in Room
        if (scannedTaxDao != null) {
            val existingScanned = scannedTaxDao.getAllScannedDocuments().firstOrNull()
            if (existingScanned.isNullOrEmpty()) {
                val now = System.currentTimeMillis()
                scannedTaxDao.insertAllScannedDocuments(
                    listOf(
                        ScannedTaxDocumentEntity(
                            title = "Form 16 Annual Salary & Tax Certificate",
                            documentType = "FORM_16",
                            category = "INCOME_TAX",
                            scanDateTimestamp = now - 3600000L * 5, // 5 hours ago
                            formattedDate = "26 Oct 2026, 09:30 AM",
                            aiSummary = "Employer: Infosys Limited • TAN: BLRI01928A • Gross Salary: ₹12,40,000 • Total TDS Deducted: ₹72,400 • Section 16(ia) Standard Deduction ₹75,000 applied • Clean reconciliation with Form 26AS.",
                            vendorOrIssuer = "Infosys Limited",
                            invoiceOrRefNumber = "F16-BLR-2025-8821",
                            detectedAmount = 1240000.0,
                            eligibleTaxSection = "Sec 16(ia) & Sec 192",
                            taxBenefitDescription = "Standard deduction of ₹75,000 and full TDS credit of ₹72,400.",
                            confidenceScore = 98,
                            status = "VERIFIED"
                        ),
                        ScannedTaxDocumentEntity(
                            title = "Star Health Insurance Premium Receipt",
                            documentType = "SECTION_80D_HEALTH",
                            category = "DEDUCTIONS",
                            scanDateTimestamp = now - 86400000L * 1, // 1 day ago
                            formattedDate = "25 Oct 2026, 03:15 PM",
                            aiSummary = "Insurer: Star Health & Allied Insurance • Policy: Family Optima Plan • Premium Paid: ₹24,800 • GST: ₹4,464 • 100% Tax Deductible under Section 80D for self and dependent family.",
                            vendorOrIssuer = "Star Health & Allied Insurance",
                            invoiceOrRefNumber = "POL-SH-2025-9941",
                            detectedAmount = 24800.0,
                            eligibleTaxSection = "Sec 80D",
                            taxBenefitDescription = "Direct Chapter VI-A deduction of ₹24,800 reducing taxable income.",
                            confidenceScore = 96,
                            status = "VERIFIED"
                        ),
                        ScannedTaxDocumentEntity(
                            title = "GST B2B Tax Invoice - Dell Technologies",
                            documentType = "GST_INVOICE",
                            category = "GST",
                            scanDateTimestamp = now - 86400000L * 3, // 3 days ago
                            formattedDate = "23 Oct 2026, 11:20 AM",
                            aiSummary = "Supplier: Dell India Pvt Ltd • GSTIN: 29AABCD1982K1Z5 • Item: Developer Workstation • Subtotal: ₹85,000 • CGST (9%): ₹7,650 • SGST (9%): ₹7,650 • Total: ₹1,00,300 • Eligible for Input Tax Credit (ITC).",
                            vendorOrIssuer = "Dell India Pvt Ltd",
                            invoiceOrRefNumber = "INV-DEL-89218",
                            detectedAmount = 100300.0,
                            eligibleTaxSection = "Sec 16 (ITC)",
                            taxBenefitDescription = "Input Tax Credit (ITC) of ₹15,300 available for GST offset.",
                            confidenceScore = 97,
                            status = "VERIFIED"
                        ),
                        ScannedTaxDocumentEntity(
                            title = "Residential House Rent Agreement & Receipt",
                            documentType = "RENT_RECEIPT_HRA",
                            category = "DEDUCTIONS",
                            scanDateTimestamp = now - 86400000L * 5, // 5 days ago
                            formattedDate = "21 Oct 2026, 02:40 PM",
                            aiSummary = "Landlord: Ramesh Verma • PAN: VWXYZ9876K • Monthly Rent: ₹28,000 • Property: Indiranagar, Bengaluru • Eligible for House Rent Allowance (HRA) exemption under Section 10(13A).",
                            vendorOrIssuer = "Ramesh Verma (Landlord)",
                            invoiceOrRefNumber = "RENT-OCT-2025",
                            detectedAmount = 28000.0,
                            eligibleTaxSection = "Sec 10(13A)",
                            taxBenefitDescription = "Eligible for House Rent Allowance (HRA) exemption under Rule 2A.",
                            confidenceScore = 95,
                            status = "VERIFIED"
                        )
                    )
                )
            }
        }

        // Seed default initial tax profile if none
        val existingTax = dao.getLatestTaxProfile().firstOrNull()
        if (existingTax == null) {
            dao.insertTaxProfile(
                TaxProfileEntity(
                    financialYear = "FY 2024-25 (AY 2025-26)",
                    salaryIncome = 1240000.0,
                    businessIncome = 0.0,
                    rentalIncome = 0.0,
                    capitalGains = 0.0,
                    otherIncome = 35000.0, // Bank savings & FD interest
                    deduction80C = 150000.0,
                    deduction80D = 25000.0,
                    deductionNps = 50000.0,
                    tdsPaid = 72400.0,
                    advanceTaxPaid = 0.0,
                    taxOldRegime = 114660.0,
                    taxNewRegime = 70720.0,
                    selectedRegime = "NEW",
                    recommendedRegime = "NEW",
                    taxSavings = 43940.0, // Substantial savings under Budget 2024 New Regime!
                    filingRoute = "ITR-1 (Sahaj)",
                    filingRouteReason = "Salaried individual with interest income and zero business earnings",
                    status = "READY_TO_FILE",
                    refundAmount = 1680.0, // TDS 72,400 - Tax 70,720 = 1,680 Refund!
                    taxPayable = 0.0
                )
            )
        }

        // Seed default sample invoice
        val existingInvoices = dao.getAllInvoices().firstOrNull()
        if (existingInvoices.isNullOrEmpty()) {
            dao.insertInvoice(
                InvoiceEntity(
                    invoiceNumber = "INV-2025-001",
                    clientName = "Nexus Technologies Pvt Ltd",
                    clientGst = "29AABCN9921K1ZZ",
                    itemsSummary = "UI/UX Design Sprint & AI Compliance Integration",
                    subtotal = 50000.0,
                    gstRatePercent = 18.0,
                    gstAmount = 9000.0,
                    totalAmount = 59000.0,
                    issueDate = "01/09/2025",
                    dueDate = "15/09/2025",
                    status = "SENT"
                )
            )
        }
    }
}

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // TAX, BUSINESS, GST, BANKING, NOTICES, PERSONAL
    val docType: String, // FORM_16, PAN, AADHAAR, GST_CERT, UDYAM_CERT, NOTICE, INVOICE, BANK_STMT
    val fileUri: String = "",
    val fileSizeFormatted: String = "2.4 MB",
    val ocrConfidence: Int = 96, // 0 to 100
    val extractedFieldsJson: String = "",
    val status: String = "VERIFIED", // SCANNED, VERIFIED, LINKED
    val uploadDate: Long = System.currentTimeMillis()
)

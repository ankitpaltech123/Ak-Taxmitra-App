package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val entityType: String, // Sole Proprietorship, Partnership, LLP, OPC, Private Limited
    val industry: String, // E-commerce, Retail, Tech SaaS, Consulting, Manufacturing, Food
    val founderCount: Int,
    val turnoverRange: String, // Under 20L, 20L-40L, 40L-1.5Cr, Above 1.5Cr
    val state: String,
    val city: String,
    val isOnline: Boolean,
    val gstRequired: Boolean,
    val gstStatus: String = "NOT_APPLIED", // NOT_APPLIED, APPLIED, APPROVED
    val udyamEligible: Boolean = true,
    val udyamStatus: String = "NOT_REGISTERED", // NOT_REGISTERED, PENDING, REGISTERED
    val trademarkStatus: String = "NOT_STARTED", // NOT_STARTED, SCREENED, FILED
    val domainName: String = "",
    val readinessScore: Int = 35, // 0 to 100
    val createdAt: Long = System.currentTimeMillis()
)

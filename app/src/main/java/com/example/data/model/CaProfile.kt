package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class CaProfile(
    val id: String,
    val name: String,
    val icaiNumber: String,
    val firmName: String,
    val experienceYears: Int,
    val city: String,
    val rating: Float,
    val reviewCount: Int,
    val isVerified: Boolean = true,
    val specializations: List<String>,
    val languages: List<String>,
    val startingFee: Double,
    val isAvailableOnline: Boolean = true,
    val bio: String
)

@Entity(tableName = "ca_engagements")
data class CaEngagementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val caId: String,
    val caName: String,
    val serviceType: String, // ITR Review, GST Filing, Notice Resolution, Business Registration
    val userMode: String = "CA_REVIEW", // DIY, AI_ASSISTED, CA_REVIEW, CA_HANDLED
    val govtFee: Double = 0.0,
    val caFee: Double = 1499.0,
    val platformFee: Double = 199.0,
    val taxes: Double = 305.0, // 18% GST on service
    val totalFee: Double = 2003.0,
    val clientNotes: String = "",
    val caRemarks: String = "",
    val status: String = "ASSIGNED", // REQUESTED, ASSIGNED, IN_PROGRESS, REVIEW_COMPLETED
    val createdAt: Long = System.currentTimeMillis()
)

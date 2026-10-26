package com.example.data.model

data class UserEntityContext(
    val id: String,
    val name: String,
    val entityType: String, // "Personal", "Proprietorship", "Private Limited", "LLP", "OPC"
    val panOrGst: String,
    val taxYear: String = "FY 2025-26 (AY 2026-27)",
    val isPrimary: Boolean = false
) {
    companion object {
        val DEFAULT_ENTITIES = listOf(
            UserEntityContext(
                id = "ent_personal",
                name = "Personal (Self)",
                entityType = "Individual",
                panOrGst = "ABCDE1234F",
                isPrimary = true
            ),
            UserEntityContext(
                id = "ent_proprietorship",
                name = "Sharma Traders",
                entityType = "Proprietorship",
                panOrGst = "07AAAAA0000A1Z5"
            ),
            UserEntityContext(
                id = "ent_pvt_ltd",
                name = "ABC Technologies Pvt Ltd",
                entityType = "Private Limited",
                panOrGst = "U72200DL2023PTC123456"
            ),
            UserEntityContext(
                id = "ent_llp",
                name = "Spark Studio Design LLP",
                entityType = "LLP",
                panOrGst = "AAB-1234"
            )
        )
    }
}

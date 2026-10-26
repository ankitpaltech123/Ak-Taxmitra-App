package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BusinessEntity
import com.example.data.model.CaEngagementEntity
import com.example.data.model.ComplianceTaskEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.ScannedTaxDocumentEntity
import com.example.data.model.TaxNoticeEntity
import com.example.data.model.TaxProfileEntity

@Database(
    entities = [
        BusinessEntity::class,
        TaxProfileEntity::class,
        DocumentEntity::class,
        InvoiceEntity::class,
        ComplianceTaskEntity::class,
        CaEngagementEntity::class,
        TaxNoticeEntity::class,
        ScannedTaxDocumentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taxmitraDao(): TaxmitraDao
    abstract fun scannedTaxDocumentDao(): ScannedTaxDocumentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taxmitra_database"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

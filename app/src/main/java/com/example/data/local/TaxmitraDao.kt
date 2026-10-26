package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BusinessEntity
import com.example.data.model.CaEngagementEntity
import com.example.data.model.ComplianceTaskEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.TaxNoticeEntity
import com.example.data.model.TaxProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxmitraDao {
    // Business
    @Query("SELECT * FROM businesses ORDER BY id DESC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses ORDER BY id DESC LIMIT 1")
    fun getPrimaryBusiness(): Flow<BusinessEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity): Long

    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    // Tax Profile
    @Query("SELECT * FROM tax_profiles ORDER BY id DESC LIMIT 1")
    fun getLatestTaxProfile(): Flow<TaxProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxProfile(profile: TaxProfileEntity): Long

    @Update
    suspend fun updateTaxProfile(profile: TaxProfileEntity)

    // Documents
    @Query("SELECT * FROM documents ORDER BY uploadDate DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE category = :category ORDER BY uploadDate DESC")
    fun getDocumentsByCategory(category: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocument(id: Long)

    // Invoices
    @Query("SELECT * FROM invoices ORDER BY id DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    // Compliance Tasks
    @Query("SELECT * FROM compliance_tasks ORDER BY dueTimestamp ASC")
    fun getAllComplianceTasks(): Flow<List<ComplianceTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplianceTasks(tasks: List<ComplianceTaskEntity>)

    @Update
    suspend fun updateComplianceTask(task: ComplianceTaskEntity)

    // CA Engagements
    @Query("SELECT * FROM ca_engagements ORDER BY id DESC")
    fun getAllCaEngagements(): Flow<List<CaEngagementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaEngagement(engagement: CaEngagementEntity): Long

    // Tax Notices
    @Query("SELECT * FROM tax_notices ORDER BY id DESC")
    fun getAllTaxNotices(): Flow<List<TaxNoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxNotice(notice: TaxNoticeEntity): Long

    @Update
    suspend fun updateTaxNotice(notice: TaxNoticeEntity)
}

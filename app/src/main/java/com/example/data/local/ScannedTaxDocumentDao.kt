package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ScannedTaxDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedTaxDocumentDao {
    @Query("SELECT * FROM scanned_tax_documents ORDER BY scanDateTimestamp DESC")
    fun getAllScannedDocuments(): Flow<List<ScannedTaxDocumentEntity>>

    @Query("SELECT * FROM scanned_tax_documents WHERE id = :id LIMIT 1")
    fun getScannedDocumentById(id: Long): Flow<ScannedTaxDocumentEntity?>

    @Query("SELECT * FROM scanned_tax_documents WHERE documentType = :documentType ORDER BY scanDateTimestamp DESC")
    fun getScannedDocumentsByType(documentType: String): Flow<List<ScannedTaxDocumentEntity>>

    @Query("SELECT * FROM scanned_tax_documents WHERE category = :category ORDER BY scanDateTimestamp DESC")
    fun getScannedDocumentsByCategory(category: String): Flow<List<ScannedTaxDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScannedDocument(document: ScannedTaxDocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllScannedDocuments(documents: List<ScannedTaxDocumentEntity>)

    @Query("DELETE FROM scanned_tax_documents WHERE id = :id")
    suspend fun deleteScannedDocumentById(id: Long)

    @Query("DELETE FROM scanned_tax_documents")
    suspend fun clearAllScannedDocuments()
}

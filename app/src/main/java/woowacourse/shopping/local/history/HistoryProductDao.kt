package woowacourse.shopping.local.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.model.HistoryProduct

@Dao
interface HistoryProductDao {
    @Insert
    fun insert(historyProduct: HistoryProduct): Long

    @Query("SELECT * from history_products WHERE id = :id")
    fun findById(id: Long): HistoryProduct?

    @Query("SELECT * from history_products ORDER BY timestamp DESC LIMIT 1")
    fun findLatest(): HistoryProduct?

    @Query("SELECT * from history_products ORDER BY timestamp DESC")
    fun findAll(): List<HistoryProduct>

    @Delete
    fun delete(historyProduct: HistoryProduct)

    @Query("DELETE FROM history_products")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert2(historyProduct: HistoryProduct): Long

    @Query("SELECT * FROM history_products ORDER BY timestamp DESC LIMIT :size")
    suspend fun findAll2(size: Int): List<HistoryProduct>

    @Query("SELECT * FROM history_products ORDER BY timestamp DESC LIMIT 1")
    suspend fun findLatest2(): HistoryProduct
}

package woowacourse.shopping.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.local.model.ProductHistoryEntity

@Dao
interface ProductHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductHistory(productHistoryEntity: ProductHistoryEntity)

    @Query("SELECT * FROM productHistoryEntity WHERE productId = :productId")
    suspend fun findProductHistory(productId: Long): ProductHistoryEntity

    @Query("SELECT * FROM productHistoryEntity WHERE category = :category ORDER BY createAt")
    suspend fun getProductHistoriesByCategory(category: String): List<ProductHistoryEntity>

    @Query("SELECT * FROM productHistoryEntity ORDER BY createAt DESC LIMIT :size")
    suspend fun getProductHistoryPaged(size: Int): List<ProductHistoryEntity>

    @Query("DELETE FROM productHistoryEntity WHERE productId = :productId")
    suspend fun deleteProductHistory(productId: Long)

    @Query("DELETE FROM productHistoryEntity")
    suspend fun deleteAllProductHistory()
}

package woowacourse.shopping.data.product.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity

@Dao
interface RecentViewedProductDao {
    @Query("SELECT * FROM recentViewedProducts")
    suspend fun loadProducts(): List<RecentViewedProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(item: RecentViewedProductEntity)

    @Query("SELECT COUNT(*) FROM recentViewedProducts")
    suspend fun productsSize(): Int

    @Query(
        "DELETE FROM recentViewedProducts WHERE productId IN (SELECT productId FROM recentViewedProducts ORDER BY viewedAt ASC LIMIT :count)",
    )
    suspend fun deleteProduct(count: Int)
}

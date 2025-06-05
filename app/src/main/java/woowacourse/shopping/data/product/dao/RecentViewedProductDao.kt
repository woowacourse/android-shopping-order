package woowacourse.shopping.data.product.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity

@Dao
interface RecentViewedProductDao {
    @Query("SELECT * FROM recentViewedProducts")
    fun loadProducts(): List<RecentViewedProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertProduct(item: RecentViewedProductEntity)

    @Query("SELECT COUNT(*) FROM recentViewedProducts")
    fun productsSize(): Int

    @Query(
        "DELETE FROM recentViewedProducts WHERE productId IN (SELECT productId FROM recentViewedProducts ORDER BY viewedAt ASC LIMIT :count)",
    )
    fun deleteProduct(count: Int)
}

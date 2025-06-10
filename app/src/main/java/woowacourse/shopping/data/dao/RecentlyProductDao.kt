package woowacourse.shopping.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.entity.RecentlyViewedProduct

@Dao
interface RecentlyProductDao {
    @Query("SELECT * FROM recentlyViewedProduct ORDER BY viewedAt DESC")
    suspend fun getProducts(): List<RecentlyViewedProduct>

    @Query("SELECT * FROM recentlyViewedProduct ORDER BY viewedAt DESC LIMIT 1")
    suspend fun getMostRecentProduct(): RecentlyViewedProduct?

    @Query("SELECT * FROM recentlyViewedProduct ORDER BY viewedAt ASC LIMIT 1")
    suspend fun getOldestProduct(): RecentlyViewedProduct

    @Query("SELECT COUNT(*) FROM recentlyViewedProduct")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: RecentlyViewedProduct)

    @Delete
    suspend fun delete(product: RecentlyViewedProduct)
}

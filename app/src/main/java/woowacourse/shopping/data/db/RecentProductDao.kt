package woowacourse.shopping.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentProductDao {
    @Query("SELECT COUNT(*) FROM RecentViewedProducts")
    suspend fun getRecentProductCount(): Int

    @Query("SELECT * FROM RecentViewedProducts ORDER BY lastViewedAt DESC LIMIT :limit")
    suspend fun getRecentProducts(limit: Int): List<RecentProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentProduct(recentProduct: RecentProductEntity)

    @Query("SELECT category FROM RecentViewedProducts ORDER BY lastViewedAt DESC LIMIT 1")
    suspend fun getRecentViewedProductCategory(): String?

    @Query(
        """
            DELETE FROM RecentViewedProducts 
            WHERE productId IN (
                SELECT productId FROM RecentViewedProducts 
                ORDER BY lastViewedAt ASC 
                LIMIT :overflowCount
            )
        """,
    )
    suspend fun deleteOldestRecentProducts(overflowCount: Int)
}

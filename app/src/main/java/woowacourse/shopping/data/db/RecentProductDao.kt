package woowacourse.shopping.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentProductDao {
    @Query(
        """
    SELECT * FROM (
        SELECT *, ROW_NUMBER() OVER (PARTITION BY productId ORDER BY viewedAt DESC) as rn
        FROM RecentProductEntity
    )
    WHERE rn = 1
    ORDER BY viewedAt DESC
    LIMIT :limit
    """,
    )
    suspend fun getRecentProducts(limit: Int): List<RecentProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentProductEntity)

    @Query(
        """
    SELECT * FROM RecentProductEntity
    WHERE productId != :currentProductId
    ORDER BY viewedAt DESC
    LIMIT 1
    """,
    )
    suspend fun getLastViewedProduct(currentProductId: Long): RecentProductEntity?

    @Query("SELECT * FROM RecentProductEntity ORDER BY viewedAt DESC LIMIT 1")
    suspend fun getMostRecentProduct(): RecentProductEntity?
}

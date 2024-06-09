package woowacourse.shopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.local.entity.RecentProductEntity

@Dao
interface RecentProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(recentProductEntity: RecentProductEntity): Long

    @Query(
        "SELECT productId, name, imgUrl, quantity, price, createdAt, category, cartId " +
            "FROM recentproductentity " +
            "ORDER BY createdAt DESC LIMIT :limit",
    )
    suspend fun findAllByLimit(limit: Int): List<RecentProductEntity>

    @Query(
        "SELECT productId, name, imgUrl, quantity, price, createdAt, category, cartId " +
            "FROM recentproductentity " +
            "ORDER BY createdAt DESC",
    )
    suspend fun findOrNull(): RecentProductEntity?
}

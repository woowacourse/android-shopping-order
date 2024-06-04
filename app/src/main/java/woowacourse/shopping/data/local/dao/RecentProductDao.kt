package woowacourse.shopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.local.entity.RecentEntity
import woowacourse.shopping.data.local.entity.RecentProductEntity

@Dao
interface RecentProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecent(recentEntity: RecentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecentProduct(recentProductEntity: RecentProductEntity)

    @Query(
        "UPDATE recentproductentity " +
                "SET quantity = :quantity, cartId = :cartId " +
                "WHERE productId = :productId",
    )
    fun updateRecentProduct(productId: Long, quantity: Int, cartId: Long)

    @Query(
        "SELECT productId, name, imgUrl, quantity, price, createdAt, category, cartId " +
                "FROM recentproductentity " +
                "ORDER BY createdAt DESC LIMIT :limit",
    )
    fun findByLimit(limit: Int): List<RecentProductEntity>

    @Query(
        "SELECT productId, name, imgUrl, quantity, price, createdAt, category, cartId " +
                "FROM recentproductentity " +
                "ORDER BY createdAt DESC",
    )
    fun findOne(): RecentProductEntity?
}

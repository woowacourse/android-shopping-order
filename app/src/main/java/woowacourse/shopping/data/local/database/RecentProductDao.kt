package woowacourse.shopping.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecentProductDao {
    @Insert
    suspend fun save(recentProductEntity: RecentProductEntity)

    @Query("UPDATE recent_products SET dateTime = :dateTime WHERE productId = :productId")
    suspend fun update(
        productId: Int,
        dateTime: String,
    )

    @Query("SELECT * FROM recent_products WHERE productId = :productId")
    suspend fun findByProductId(productId: Int): RecentProductEntity?

    @Query("SELECT * FROM recent_products ORDER BY dateTime DESC LIMIT 1")
    suspend fun findMostRecentProduct(): RecentProductEntity?

    @Query("SELECT * FROM recent_products ORDER BY dateTime DESC LIMIT :limit")
    suspend fun findAll(limit: Int): List<RecentProductEntity>

    @Query("DELETE FROM recent_products")
    suspend fun deleteAll()
}

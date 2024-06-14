package woowacourse.shopping.data.database.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import woowacourse.shopping.data.database.entity.RecentProductEntity

@Dao
interface RecentProductDao {
    @Insert
    suspend fun save(recentProductEntity: RecentProductEntity)

    @Query("SELECT * FROM recent_products ORDER BY id DESC LIMIT 1")
    suspend fun loadLatest(): RecentProductEntity?

    @Query("SELECT * FROM recent_products ORDER BY id DESC LIMIT 1 OFFSET 1")
    suspend fun loadSecondLatest(): RecentProductEntity?

    @Query("SELECT * FROM recent_products DISTICT ORDER BY id DESC LIMIT 10")
    suspend fun loadLatestList(): List<RecentProductEntity>

    @Query("DELETE FROM recent_products WHERE productId = :productId")
    suspend fun deleteWithProductId(productId: Long)
}

package woowacourse.shopping.data.recent.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity
import java.time.LocalDateTime

@Dao
interface RecentProductDao {
    @Query("SELECT * FROM recent_products WHERE product_id = :productId")
    fun findOrNull(productId: Int): RecentProductEntity?

    @Query(
        """
        SELECT *
        FROM recent_products
        ORDER BY seen_date_time DESC
        LIMIT :size
    """,
    )
    fun findRange(size: Int): List<RecentProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentProductEntity: RecentProductEntity)

    @Query("UPDATE recent_products SET seen_date_time = :seenDateTime WHERE product_id = :productId")
    fun update(
        productId: Int,
        seenDateTime: LocalDateTime,
    )
}

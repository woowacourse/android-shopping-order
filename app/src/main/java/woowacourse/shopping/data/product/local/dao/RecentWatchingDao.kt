package woowacourse.shopping.data.product.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity

@Dao
interface RecentWatchingDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertRecentWatching(recentWatchingEntity: RecentWatchingEntity)

    @Query(
        """
    SELECT * FROM recent_watching
    WHERE category = (
        SELECT category FROM recent_watching
        ORDER BY watchedAt DESC
        LIMIT 1
    )
    ORDER BY watchedAt DESC
    LIMIT :size
""",
    )
    fun getRecentRecommendWatchingProducts(size: Int): List<RecentWatchingEntity>

    @Query("SELECT * FROM recent_watching ORDER BY watchedAt DESC LIMIT :size")
    fun getRecentWatchingProducts(size: Int): List<RecentWatchingEntity>
}

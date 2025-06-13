package woowacourse.shopping.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity

@Dao
interface RecentlyViewedProductDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertRecentlyViewedProductUid(recentlyViewedProductEntity: RecentlyViewedProductEntity)

    @Query("SELECT productUid FROM RecentlyViewedProducts ORDER BY timestamp DESC")
    suspend fun getRecentlyViewedProductUids(): List<Int>

    @Query("SELECT productUid FROM RecentlyViewedProducts ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestViewedProductUid(): Int
}

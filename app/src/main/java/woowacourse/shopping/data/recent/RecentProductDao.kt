package woowacourse.shopping.data.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentProductDao {
    @Query("SELECT * FROM recentProducts ORDER BY datetime(date_time) DESC LIMIT 10;")
    suspend fun loadAll(): List<RecentProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentProduct: RecentProductEntity): Long

    @Query("SELECT * FROM recentProducts ORDER BY datetime(date_time) DESC LIMIT 1;")
    suspend fun getMostRecent(): RecentProductEntity?
}

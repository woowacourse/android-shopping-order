package woowacourse.shopping.data.source.local.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentProductDao {
    @Query("SELECT * FROM recent_products ORDER BY lastViewedAt DESC LIMIT :limit")
    fun getRecentStream(limit: Int = 10): Flow<List<RecentProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecentProduct(entity: RecentProductEntity)
}

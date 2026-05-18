package woowacourse.shopping.data.localdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.data.localdb.entity.RecentItemEntity

@Dao
interface RecentItemDao {
    @Upsert
    suspend fun upsert(item: RecentItemEntity)

    @Query("SELECT * FROM recent_items ORDER BY timestamp DESC, id DESC LIMIT 10")
    fun getRecentItems(): Flow<List<RecentItemEntity>>

    @Query("SELECT * FROM recent_items WHERE id = :id")
    suspend fun getRecentItemById(id: String): RecentItemEntity?

    @Query(
        """
        DELETE FROM recent_items
        WHERE id NOT IN (SELECT id FROM recent_items ORDER BY timestamp DESC, id DESC LIMIT 10)
    """,
    )
    suspend fun deleteOldItem()

    @Query(
        """
            SELECT * FROM recent_items
            ORDER BY timestamp DESC, id DESC
            LIMIT 1
        """,
    )
    suspend fun getLastViewedItem(): RecentItemEntity?
}

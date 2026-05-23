package woowacourse.shopping.data.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.data.localdb.entity.RecentItemEntity

@Dao
interface RecentItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentItemEntity)

    @Query("SELECT * FROM recent_items ORDER BY timestamp DESC, productId DESC LIMIT :limit")
    fun getRecentItems(limit: Int): Flow<List<RecentItemEntity>>

    @Query("SELECT * FROM recent_items WHERE productId = :productId")
    suspend fun getRecentItemByProductId(productId: Long): RecentItemEntity?

    @Query(
        """
        DELETE FROM recent_items
        WHERE productId NOT IN (SELECT productId FROM recent_items ORDER BY timestamp DESC, productId DESC LIMIT :limit)
    """,
    )
    suspend fun deleteItemsExceedingLimit(limit: Int)

    @Query(
        """
            SELECT * FROM recent_items
            ORDER BY timestamp DESC, productId DESC
            LIMIT 1
        """,
    )
    suspend fun getLastViewedItem(): RecentItemEntity?
}

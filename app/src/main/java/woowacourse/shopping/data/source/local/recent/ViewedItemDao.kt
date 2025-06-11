package woowacourse.shopping.data.source.local.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.model.ViewedItem

@Dao
interface ViewedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewedProduct(viewedItem: ViewedItem)

    @Query("SELECT * FROM ViewedItemEntity ORDER BY viewedAt DESC LIMIT 10")
    suspend fun getRecentViewedItems(): List<ViewedItem>

    @Query("SELECT * FROM ViewedItemEntity ORDER BY viewedAt DESC LIMIT 1")
    suspend fun getLastViewedItem(): ViewedItem?
}

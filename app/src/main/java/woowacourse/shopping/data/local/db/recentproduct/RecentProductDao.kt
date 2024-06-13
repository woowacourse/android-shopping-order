package woowacourse.shopping.data.local.db.recentproduct

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import woowacourse.shopping.data.model.RecentProduct

@Dao
interface RecentProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentProduct: RecentProduct): Long

    @Query("SELECT * FROM recent_products ORDER BY recentTime DESC LIMIT 1")
    suspend fun findMostRecentProduct(): RecentProduct?

    @Query("SELECT * FROM recent_products ORDER BY recentTime DESC")
    suspend fun findAll(): List<RecentProduct>

    @Query("DELETE FROM recent_products")
    suspend fun deleteAll()
}

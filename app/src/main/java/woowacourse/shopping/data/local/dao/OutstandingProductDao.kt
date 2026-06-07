package woowacourse.shopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import woowacourse.shopping.data.local.entity.OutstandingProductEntity

@Dao
interface OutstandingProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<OutstandingProductEntity>)

    @Query("SELECT * FROM outstanding_products")
    suspend fun getAll(): List<OutstandingProductEntity>

    @Query("DELETE FROM outstanding_products")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(entities: List<OutstandingProductEntity>) {
        deleteAll()
        insertAll(entities)
    }
}

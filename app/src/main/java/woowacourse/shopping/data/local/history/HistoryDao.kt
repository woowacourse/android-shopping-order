package woowacourse.shopping.data.local.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    suspend fun getAll(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg historyEntity: HistoryEntity)

    @Query("SELECT * FROM history WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): HistoryEntity?

    @Query("DELETE FROM history WHERE id = (SELECT id FROM history ORDER BY timestamp ASC LIMIT 1)")
    fun deleteOldest()

    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 1")
    suspend fun findLatest(): HistoryEntity?
}

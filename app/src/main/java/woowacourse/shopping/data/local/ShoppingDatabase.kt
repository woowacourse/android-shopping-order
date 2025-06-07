package woowacourse.shopping.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.local.history.HistoryDao
import woowacourse.shopping.data.local.history.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        private const val DATABASE_NAME = "app_database"

        @Volatile
        private var instance: ShoppingDatabase? = null

        fun getDatabase(context: Context): ShoppingDatabase =
            instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context.applicationContext,
                        ShoppingDatabase::class.java,
                        DATABASE_NAME,
                    ).build()
                    .also { instance = it }
            }
    }
}

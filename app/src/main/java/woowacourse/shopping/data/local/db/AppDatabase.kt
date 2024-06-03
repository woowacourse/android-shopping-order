package woowacourse.shopping.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.RecentProductEntity
import java.lang.IllegalStateException

@Database(
    entities = [RecentProductEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentProductDao(): RecentProductDao

    companion object {
        @Volatile
        private var _instance: AppDatabase? = null
        val instance: AppDatabase get() = _instance ?: throw IllegalStateException("DB is not initialized")

        fun init(context: Context): AppDatabase {
            return _instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { _instance = it }
            }
        }
    }
}

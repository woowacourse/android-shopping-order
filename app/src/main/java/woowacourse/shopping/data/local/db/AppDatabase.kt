package woowacourse.shopping.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.local.dao.CartProductDao
import woowacourse.shopping.data.local.dao.RecentProductDao
import woowacourse.shopping.data.local.entity.CartEntity
import woowacourse.shopping.data.local.entity.CartProductEntity
import woowacourse.shopping.data.local.entity.ProductEntity
import woowacourse.shopping.data.local.entity.RecentEntity
import woowacourse.shopping.data.local.entity.RecentProductEntity

@Database(
    entities = [CartEntity::class, CartProductEntity::class, ProductEntity::class, RecentProductEntity::class, RecentEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartProductDao(): CartProductDao

    abstract fun recentProductDao(): RecentProductDao

    companion object {
        @Volatile
        private var _instance: AppDatabase? = null
        val instance: AppDatabase get() = _instance ?: throw IllegalStateException("DB is not initialized")

        fun init(context: Context): AppDatabase {
            return _instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "db")
                    .build()
                    .also { _instance = it }
            }
        }
    }
}

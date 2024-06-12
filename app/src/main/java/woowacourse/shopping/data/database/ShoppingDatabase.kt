package woowacourse.shopping.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.database.recent.RecentProductDao
import woowacourse.shopping.data.model.entity.RecentProductEntity

@Database(
    entities = [
        RecentProductEntity::class,
    ],
    version = 1,
)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun recentProductDao(): RecentProductDao

    companion object {
        private var instance: ShoppingDatabase? = null

        fun getInstance(context: Context): ShoppingDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ShoppingDatabase::class.java,
                    "shopping_database",
                ).build().also { instance = it }
            }
        }
    }
}

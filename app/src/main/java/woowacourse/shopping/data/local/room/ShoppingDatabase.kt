package woowacourse.shopping.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.local.room.cart.CartItemDao
import woowacourse.shopping.data.local.room.cart.CartItemEntity
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDao
import woowacourse.shopping.data.local.room.recentproduct.RecentProductEntity

@Database(
    entities = [CartItemEntity::class, RecentProductEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    abstract fun recentProductDao(): RecentProductDao

    companion object {
        @Volatile
        private var instance: ShoppingDatabase? = null

        fun getInstance(context: Context): ShoppingDatabase =
            instance ?: synchronized(this) {
                instance
                    ?: Room
                        .databaseBuilder(
                            context.applicationContext,
                            ShoppingDatabase::class.java,
                            "shopping.db",
                        ).fallbackToDestructiveMigration(dropAllTables = true)
                        .build()
                        .also { instance = it }
            }
    }
}

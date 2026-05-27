package woowacourse.shopping.data.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.localdb.dao.CartItemQuantityDao
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.entity.CartItemQuantityEntity
import woowacourse.shopping.data.localdb.entity.RecentItemEntity

@Database(
    entities = [CartItemQuantityEntity::class, RecentItemEntity::class],
    version = 1,
)
abstract class ShoppingDB : RoomDatabase() {
    abstract fun recentItemDao(): RecentItemDao

    abstract fun cartItemQuantityDao(): CartItemQuantityDao

    companion object {
        @Volatile
        private var instance: ShoppingDB? = null

        fun getInstance(context: Context): ShoppingDB =
            instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(
                        context.applicationContext,
                        ShoppingDB::class.java,
                        "shopping_db",
                    ).build()
                    .also {
                        instance = it
                    }
            }
    }
}

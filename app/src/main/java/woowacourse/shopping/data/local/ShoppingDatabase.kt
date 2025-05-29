package woowacourse.shopping.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.data.local.cart.CartDao
import woowacourse.shopping.data.local.cart.CartEntity
import woowacourse.shopping.data.local.history.HistoryDao
import woowacourse.shopping.data.local.history.HistoryEntity

@Database(entities = [CartEntity::class, HistoryEntity::class], version = 1)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var instance: ShoppingDatabase? = null

        fun getDatabase(context: Context): ShoppingDatabase =
            instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context.applicationContext,
                        ShoppingDatabase::class.java,
                        "app_database",
                    ).build()
                    .also { instance = it }
            }
    }
}

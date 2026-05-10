package woowacourse.shopping.storage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.storage.room.shoppingItem.ShoppingItemEntity
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartEntity

@Database(
    entities = [ShoppingItemEntity::class, ShoppingCartEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun shoppingItemDao(): ShoppingItemDao

    abstract fun shoppingCartDao(): ShoppingCartDao

    companion object {
        private const val DATABASE_NAME = "shopping.db"

        fun create(context: Context): ShoppingDatabase =
            Room
                .databaseBuilder(context, ShoppingDatabase::class.java, DATABASE_NAME)
                .build()
    }
}


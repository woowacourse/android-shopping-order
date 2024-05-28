package woowacourse.shopping.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import woowacourse.shopping.data.cart.local.converter.CartItemConverter
import woowacourse.shopping.data.cart.local.dao.CartDao
import woowacourse.shopping.data.cart.local.entity.CartItemEntity
import woowacourse.shopping.data.product.local.dao.ProductDao
import woowacourse.shopping.data.product.local.entity.ProductEntity
import woowacourse.shopping.data.recent.local.converter.RecentProductConverter
import woowacourse.shopping.data.recent.local.dao.RecentProductDao
import woowacourse.shopping.data.recent.local.entity.RecentProductEntity

@Database(entities = [CartItemEntity::class, ProductEntity::class, RecentProductEntity::class], version = 2)
@TypeConverters(CartItemConverter::class, RecentProductConverter::class)
abstract class ShoppingCartDataBase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    abstract fun productDao(): ProductDao

    abstract fun recentProductDao(): RecentProductDao

    companion object {
        @Volatile
        private var instance: ShoppingCartDataBase? = null

        fun instance(context: Context): ShoppingCartDataBase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(context, ShoppingCartDataBase::class.java, "shopping_cart").build()
                instance = newInstance
                newInstance
            }
        }
    }
}

package woowacourse.shopping.data.cart.database

import androidx.room.Database
import androidx.room.RoomDatabase
import woowacourse.shopping.data.cart.dao.CartDao
import woowacourse.shopping.data.product.entity.CartItemEntity

@Database(entities = [CartItemEntity::class], version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun dao(): CartDao
}

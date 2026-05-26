package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.local.room.ShoppingDatabase

object DatabaseProvider {
    fun provide(context: Context): ShoppingDatabase = ShoppingDatabase.getInstance(context)
}

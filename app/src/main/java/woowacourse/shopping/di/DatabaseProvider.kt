package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.database.ShoppingDatabase

object DatabaseProvider {
    lateinit var database: ShoppingDatabase
        private set

    fun init(context: Context) {
        database = ShoppingDatabase.getInstance(context)
    }
}

package woowacourse.shopping.di

import android.content.Context
import woowacourse.shopping.data.database.ShoppingDatabase

object DatabaseInjection {
    private var _database: ShoppingDatabase? = null
    val database: ShoppingDatabase get() = _database ?: throw IllegalStateException()

    fun init(context: Context) {
        if (_database == null) {
            _database = ShoppingDatabase.getInstance(context.applicationContext)
        }
    }
}

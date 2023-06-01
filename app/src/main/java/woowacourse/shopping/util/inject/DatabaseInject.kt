package woowacourse.shopping.util.inject

import android.content.Context
import woowacourse.shopping.data.database.ShoppingDatabase

fun injectShoppingDatabase(context: Context): ShoppingDatabase {
    return ShoppingDatabase.get(context)
}

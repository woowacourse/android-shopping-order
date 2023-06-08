package woowacourse.shopping.data.local

import android.content.Context
import woowacourse.shopping.data.database.DbHelper

class LocalDataSourceContainer(context: Context) {
    private val dbHelper = DbHelper(context).writableDatabase
    val recentlyViewedProduct = RecentlyViewedProductMemorySource(dbHelper)
    val user = UserMemorySource()
}

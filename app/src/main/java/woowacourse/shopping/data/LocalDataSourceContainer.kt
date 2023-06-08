package woowacourse.shopping.data

import android.content.Context
import woowacourse.shopping.data.database.DbHelper
import woowacourse.shopping.data.local.RecentlyViewedProductMemorySource
import woowacourse.shopping.data.local.UserMemorySource

class LocalDataSourceContainer(context: Context) {
    private val dbHelper = DbHelper(context).writableDatabase
    val recentlyViewedProduct = RecentlyViewedProductMemorySource(dbHelper)
    val user = UserMemorySource()
}

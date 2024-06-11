package woowacourse.shopping.data.shopping.recent

import android.content.Context
import woowacourse.shopping.local.ShoppingDatabase

object RecentProductDataSourceInjector {
    @Volatile
    private var instance: RecentProductDataSource? = null

    fun recentProductDataSource(context: Context): RecentProductDataSource =
        instance ?: synchronized(this) {
            instance ?: DefaultRecentProductDataSource(
                ShoppingDatabase.instance(context).recentProductDao(),
            ).also { instance = it }
        }
}

package woowacourse.shopping.util.inject

import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.database.dao.recentproduct.RecentProductDao
import woowacourse.shopping.data.database.dao.recentproduct.RecentProductDaoImpl

fun injectRecentProductDao(database: ShoppingDatabase): RecentProductDao =
    RecentProductDaoImpl(database)

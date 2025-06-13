package woowacourse.shopping.data.goods.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.domain.model.Goods

class GoodsLocalDataSourceImpl(
    private val shoppingDatabase: ShoppingDatabase,
) : GoodsLocalDataSource {

    override suspend fun fetchRecentGoodsIds(): List<String> = withContext(Dispatchers.IO) {
        shoppingDatabase.recentSeenGoodsDao().getRecentGoodsIds()
    }

    override suspend fun loggingRecentGoods(goods: Goods) = withContext(Dispatchers.IO) {
        shoppingDatabase
            .recentSeenGoodsDao()
            .addRecentGoodsWithLimit(goods.id.toString())
    }
}
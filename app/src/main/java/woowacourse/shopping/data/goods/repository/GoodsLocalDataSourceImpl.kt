package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.domain.model.Goods

class GoodsLocalDataSourceImpl(
    private val shoppingDatabase: ShoppingDatabase,
) : GoodsLocalDataSource {
    override suspend fun fetchRecentGoodsIds(): List<String> = shoppingDatabase.recentSeenGoodsDao().getRecentGoodsIds()

    override suspend fun loggingRecentGoods(goods: Goods) {
        shoppingDatabase
            .recentSeenGoodsDao()
            .addRecentGoodsWithLimit(goods.id.toString())
    }
}

package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.domain.model.Goods

interface GoodsLocalDataSource {
    suspend fun fetchRecentGoodsIds(): List<String>

    suspend fun loggingRecentGoods(goods: Goods)
}

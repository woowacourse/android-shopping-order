package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.domain.model.Goods

interface GoodsLocalDataSource {
    suspend fun fetchRecentGoodsIds(): List<String>

    fun loggingRecentGoods(
        goods: Goods,
        onComplete: () -> Unit,
    )
}

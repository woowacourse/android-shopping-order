package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.domain.model.Goods

interface GoodsRepository {
    fun fetchGoodsSize(onComplete: (Int) -> Unit)

    suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): GoodsResponse

    suspend fun fetchCategoryGoods(
        limit: Int,
        category: String,
    ): GoodsResponse

    fun fetchGoodsById(
        id: Int,
        onComplete: (Goods?) -> Unit,
    )

    fun fetchRecentGoodsIds(onComplete: (List<String>) -> Unit)

    fun fetchRecentGoods(onComplete: (List<Goods>) -> Unit)

    fun fetchMostRecentGoods(onComplete: (Goods?) -> Unit)

    fun loggingRecentGoods(
        goods: Goods,
        onComplete: () -> Unit,
    )
}

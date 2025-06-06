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

    suspend fun fetchGoodsByGoodsId(id: Int): Goods?

    suspend fun fetchRecentGoodsIds(): List<String>

    suspend fun fetchRecentGoods(): List<Goods>

    suspend fun fetchMostRecentGoods(): Goods?

    fun loggingRecentGoods(
        goods: Goods,
        onComplete: () -> Unit,
    )
}

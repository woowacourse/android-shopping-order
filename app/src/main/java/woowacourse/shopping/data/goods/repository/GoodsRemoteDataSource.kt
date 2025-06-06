package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse

interface GoodsRemoteDataSource {
    fun fetchGoodsSize(onComplete: (Int) -> Unit)

    suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): GoodsResponse

    suspend fun fetchGoodsByCategory(
        limit: Int,
        category: String,
    ): GoodsResponse

    fun fetchGoodsById(
        id: Int,
        onComplete: (Content) -> Unit,
    )
}

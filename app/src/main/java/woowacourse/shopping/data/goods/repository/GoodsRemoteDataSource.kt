package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse

interface GoodsRemoteDataSource {
    suspend fun fetchGoodsSize(): Int

    suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): GoodsResponse

    suspend fun fetchGoodsByCategory(
        limit: Int,
        category: String,
    ): GoodsResponse

    suspend fun fetchGoodsById(
        id: Int,
    ): Content
}
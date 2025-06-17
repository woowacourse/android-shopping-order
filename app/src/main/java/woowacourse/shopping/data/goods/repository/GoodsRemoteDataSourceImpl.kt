package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService

class GoodsRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : GoodsRemoteDataSource {
    override suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): GoodsResponse =
        retrofitService
            .requestProducts(page = offset / limit, size = limit)

    override suspend fun fetchGoodsByCategory(
        limit: Int,
        category: String,
    ): GoodsResponse =
        retrofitService
            .requestProducts(
                page = 0,
                size = limit,
                category = category,
            )

    override suspend fun fetchGoodsDetailByGoodsId(goodsId: Int): Content =
        retrofitService
            .requestProductDetail(
                id = goodsId.toLong(),
            )
}

package woowacourse.shopping.data.goods.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.data.util.RetrofitService

class GoodsRemoteDataSourceImpl(
    private val baseUrl: String = BuildConfig.BASE_URL,
) : GoodsRemoteDataSource {

    private val retrofitService: RetrofitService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    override suspend fun fetchGoodsSize(): Int {
        val response = retrofitService.requestProducts()
        return response.totalElements
    }

    override suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): GoodsResponse {
        return retrofitService.requestProducts(
            page = offset / limit,
            size = limit,
        )
    }

    override suspend fun fetchGoodsByCategory(
        limit: Int,
        category: String,
    ): GoodsResponse {
        return retrofitService.requestProducts(
            page = 0,
            size = limit,
            category = category
        )
    }

    override suspend fun fetchGoodsById(id: Int): Content {
        return retrofitService.requestProductDetail(id = id.toLong())
    }
}

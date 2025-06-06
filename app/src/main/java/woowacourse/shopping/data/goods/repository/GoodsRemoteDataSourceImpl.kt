package woowacourse.shopping.data.goods.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.data.util.HeaderInterceptor
import woowacourse.shopping.data.util.RetrofitService

class GoodsRemoteDataSourceImpl(
    baseUrl: String = BuildConfig.BASE_URL,
) : GoodsRemoteDataSource {
    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor())
            .build()

    private val retrofitService: RetrofitService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RetrofitService::class.java)

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

    override fun fetchGoodsSize(onComplete: (Int) -> Unit) {
    }

    override fun fetchGoodsById(
        id: Int,
        onComplete: (Content) -> Unit,
    ) {
        retrofitService
            .requestProductDetail(
                id = id.toLong(),
            ).enqueue(
                object : Callback<Content> {
                    override fun onResponse(
                        call: Call<Content>,
                        response: Response<Content>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            onComplete(response.body()!!)
                        }
                    }

                    override fun onFailure(
                        call: Call<Content>,
                        t: Throwable,
                    ) {
                        println("error : $t")
                    }
                },
            )
    }
}

package woowacourse.shopping.data.goods.repository

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.data.util.RetrofitService

class GoodsRemoteDataSourceImpl(
    private val baseUrl: String = BuildConfig.BASE_URL,
) : GoodsRemoteDataSource {
    val retrofitService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    private val gson = Gson()

    override fun fetchPageGoods(
        limit: Int,
        offset: Int,
        onSuccess: (GoodsResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        retrofitService
            .requestProducts(page = offset / limit, size = limit)
            .enqueue(
                object : Callback<GoodsResponse> {
                    override fun onResponse(
                        call: Call<GoodsResponse>,
                        response: Response<GoodsResponse>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            onSuccess(response.body()!!)
                        } else {
                            onFailure(Throwable("응답 없음 또는 실패: ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<GoodsResponse>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun fetchGoodsByCategory(
        limit: Int,
        category: String,
        onSuccess: (GoodsResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        retrofitService
            .requestProducts(
                page = 0,
                size = limit,
                category = category,
            ).enqueue(
                object : Callback<GoodsResponse> {
                    override fun onResponse(
                        call: Call<GoodsResponse>,
                        response: Response<GoodsResponse>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            onSuccess(response.body()!!)
                        } else {
                            onFailure(Throwable("응답 없음 또는 실패: ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<GoodsResponse>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

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

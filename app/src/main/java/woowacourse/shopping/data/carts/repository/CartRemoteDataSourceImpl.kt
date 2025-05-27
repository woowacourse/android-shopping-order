package woowacourse.shopping.data.carts.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.util.RetrofitService

class CartRemoteDataSourceImpl(
    private val baseUrl: String = BuildConfig.BASE_URL,
) : CartRemoteDataSource {
    val retrofitService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    override fun fetchCartItemSize(onComplete: (Int) -> Unit) {
        // Todo
    }

    override fun fetchPageCartItem(
        limit: Int,
        offset: Int,
        onSuccess: (CartResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        retrofitService
            .requestCartProduct(page = offset / limit, size = limit, authorization = "Basic bWVkQW5kcm86cGFzc3dvcmQ=")
            .enqueue(
                object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            onSuccess(response.body()!!)
                        } else {
                            onFailure(Throwable("응답 없음 또는 실패: ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<CartResponse>,
                        t: Throwable,
                    ) {
                        print(t)
                        onFailure(t)
                    }
                },
            )
    }

    override fun fetchGoodsCount(onSuccess: (Int) -> Unit) {
        // Todo
    }

    override fun increaseItemCount(itemId: Int) {
        // Todo
    }

    override fun decreaseItemCount(itemId: Int) {
        // Todo
    }

    override fun deleteItem(itemId: Int) {
        // Todo
    }

    override fun addItem(itemId: Int) {
        // Todo
    }
}

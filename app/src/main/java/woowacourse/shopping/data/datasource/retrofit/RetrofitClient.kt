package woowacourse.shopping.data.datasource.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var BASE_URL = "http://3.34.134.115:8080/"
    internal var retrofit: Retrofit = createRetrofit()

    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun setBaseUrl(baseUrl: String) {
        BASE_URL = baseUrl
        retrofit = createRetrofit()
    }

    internal inline fun <reified T> create(): T = retrofit.create<T>(T::class.java)
}

object ServicePool {
    val productDataService = RetrofitClient.create<ProductDataService>()
    val productDetailService = RetrofitClient.create<ProductDetailService>()
    val shoppingCartService = RetrofitClient.create<ShoppingCartService>()
    val orderDataService = RetrofitClient.create<OrderDataService>()
    val orderDetailService = RetrofitClient.create<OrderDetailService>()
    val couponDataService = RetrofitClient.create<CouponDataService>()
    val orderHistoryService = RetrofitClient.create<OrderHistoryService>()
}

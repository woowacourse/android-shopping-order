package woowacourse.shopping.data.datasource.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {

    private var BASE_URL = "http://3.34.134.115:8080/"
    internal var retrofit: Retrofit = createRetrofit()

    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
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

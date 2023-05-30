package woowacourse.shopping.data.datasource.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://3.34.134.115:8080"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    inline fun <reified T> create(): T = retrofit.create<T>(T::class.java)
}

object ServicePool {
    val productDataService = RetrofitClient.create<ProductDataService>()
    val productDetailService = RetrofitClient.create<ProductDetailService>()
    val shoppingCartService = RetrofitClient.create<ShoppingCartService>()
}

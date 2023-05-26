package woowacourse.shopping.service

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.model.Product

interface RetrofitProductService {
    @GET("products")
    fun getProducts(): Call<List<Product>>

    @GET("products/{productId}")
    fun getProduct(productId: Int): Call<Product>
}

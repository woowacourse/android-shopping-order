package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.product.response.GetProductResponse

interface ProductService {
    @GET("products")
    fun requestProducts(): Call<List<GetProductResponse>>

    @GET("products/{id}")
    fun requestProduct(@Path("id") id: Int): Call<GetProductResponse>
}
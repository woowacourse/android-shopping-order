package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.remote.response.ProductResponseDTO

interface ProductDetailService {
    @GET("/products/{productId}")
    fun getProductById(@Path("productId") id: Long): Call<ProductResponseDTO>
}

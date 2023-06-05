package woowacourse.shopping.data.datasource.remote.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDetailService {
    @GET("/products/{productId}")
    fun getProductById(@Path("productId") id: Long): Call<ProductDTO>
}

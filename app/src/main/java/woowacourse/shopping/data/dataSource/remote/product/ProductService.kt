package woowacourse.shopping.data.dataSource.remote.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.model.dto.response.ProductDto

interface ProductService {
    @GET("products")
    fun getAllProducts(): Call<List<ProductDto>>

    @GET("products/{productId}")
    fun getProductById(@Path("productId") productId: Long): Call<ProductDto>
}

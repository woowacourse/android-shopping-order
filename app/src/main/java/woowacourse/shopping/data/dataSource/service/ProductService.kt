package woowacourse.shopping.data.dataSource.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.model.dto.request.product.ProductDto

interface ProductService {
    @GET("products")
    fun getAllProducts(): Call<List<ProductDto>>

    @GET("products/{productId}")
    fun getProductById(@Path("productId") productId: Long): Call<ProductDto>
}

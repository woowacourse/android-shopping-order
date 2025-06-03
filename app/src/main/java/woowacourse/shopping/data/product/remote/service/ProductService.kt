package woowacourse.shopping.data.product.remote.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.remote.dto.ProductResponseDto
import woowacourse.shopping.data.product.remote.dto.ProductsResponseDto

interface ProductService {
    @GET("products")
    fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductsResponseDto>

    @GET("products/{id}")
    fun getProductDetail(
        @Path("id") productId: Long,
    ): Call<ProductResponseDto>
}

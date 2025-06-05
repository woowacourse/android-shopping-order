package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponseDto

interface ProductApiService {
    @GET("/products")
    fun getPagedProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): Call<ProductResponseDto>

    @GET("/products/{id}")
    fun getProductById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<ProductDto>
}

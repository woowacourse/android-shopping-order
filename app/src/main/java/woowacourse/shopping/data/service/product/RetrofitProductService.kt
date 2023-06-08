package woowacourse.shopping.data.service.product

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.dto.ProductsDto

interface RetrofitProductService {
    @GET("/products")
    fun requestProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductsDto>

    @GET("/products/{productId}")
    fun requestProductById(@Path("productId") productId: String): Call<ProductDto?>

    @POST("/products")
    fun insertProduct(@Body product: ProductDto): Call<ProductDto>

    @PUT("/products/{productId}")
    fun updateProduct(
        @Path("productId") productId: String,
        @Body product: ProductDto,
    ): Call<ProductDto>

    @DELETE("/products/{productId}")
    fun deleteProduct(
        @Path("productId") productId: String,
    ): Call<ProductDto>
}

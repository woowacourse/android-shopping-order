package woowacourse.shopping.data.service.retrofit.product

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import woowacourse.shopping.data.dto.ProductDto

interface RetrofitProductService {
    @GET("products")
    fun requestProducts(): Call<List<ProductDto>>

    @GET("products/{productId}")
    fun requestProductById(@Path("productId") productId: String): Call<ProductDto?>

    @POST("products")
    fun insertProduct(@Body product: ProductDto): Call<ProductDto>

    @PUT("products/{productId}")
    fun updateProduct(
        @Path("productId") productId: String,
        @Body product: ProductDto,
    ): Call<ProductDto>

    @DELETE("products/{productId}")
    fun deleteProduct(
        @Path("productId") productId: String,
    ): Call<ProductDto>
}

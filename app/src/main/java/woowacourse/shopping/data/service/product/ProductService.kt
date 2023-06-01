package woowacourse.shopping.data.service.product

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import woowacourse.shopping.data.dto.ProductDeleteRequest
import woowacourse.shopping.data.dto.ProductGetResponse
import woowacourse.shopping.data.dto.ProductPostRequest
import woowacourse.shopping.data.dto.ProductPutRequest
import woowacourse.shopping.data.service.cart.ProductId

interface ProductService {
    @GET("/products")
    fun getAllProduct(): Call<List<ProductGetResponse>>

    @GET("/products/{productId}")
    fun findProductById(@Path("productId") id: ProductId): Call<ProductGetResponse?>

    @POST("/products")
    fun saveProduct(@Body product: ProductPostRequest): Call<Unit>

    @PUT("/products/{productId}")
    fun updateProduct(
        @Path("productId") productId: ProductId,
        @Body product: ProductPutRequest,
    ): Call<Unit>

    @DELETE("/products/{productId}")
    fun deleteProduct(
        @Path("productId") productId: ProductId,
        @Body product: ProductDeleteRequest,
    ): Call<Unit>
}

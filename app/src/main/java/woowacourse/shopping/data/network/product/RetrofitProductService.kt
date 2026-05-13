package woowacourse.shopping.data.network.product

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.network.cart.dto.ProductDto
import woowacourse.shopping.data.network.product.dto.ProductResponse

interface RetrofitProductService {
    @GET("/products")
    fun requestProducts(
        @Header("accept")
        accept: String = "*/*",
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 1,
    ): Call<ProductResponse>

    @POST("/products")
    fun insertProducts(
        @Header("accept")
        accept: String = "*/*",
        @Body
        product: ProductDto,
    ): Call<Unit>

    @GET("/products/{id}")
    fun getProductDetail(
        @Header("accept")
        accept: String = "*/*",
        @Path("id")
        id: String = "",
    ): Call<ProductDto>

    @DELETE("/products/{id}")
    fun deleteProductDetail(
        @Header("accept")
        accept: String = "*/*",
        @Path("id")
        id: String = "",
    ): Call<Unit>
}

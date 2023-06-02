package woowacourse.shopping.server.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.model.data.dto.ProductDTO

interface ProductsService {

    @GET("/products")
    fun getAllProducts(): Call<List<ProductDTO>>

    @GET("/products/{productId}")
    fun getProduct(
        @Path("productId") productId: Long
    ): Call<ProductDTO>
}

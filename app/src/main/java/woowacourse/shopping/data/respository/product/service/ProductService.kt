package woowacourse.shopping.data.respository.product.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.model.dto.response.ProductResponse

interface ProductService {
    @GET("/products")
    fun requestDatas(): Call<List<ProductResponse>>

    @GET("/products/{productId}")
    fun requestData(
        @Path("productId") productId: Long
    ): Call<ProductResponse>
}

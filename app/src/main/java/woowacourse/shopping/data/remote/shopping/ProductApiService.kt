package woowacourse.shopping.data.remote.shopping

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductResponse

interface ProductApiService {
    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun requestProductDetail(
        @Path(value = "id") productId: Int,
    ): Call<ProductDto>
}

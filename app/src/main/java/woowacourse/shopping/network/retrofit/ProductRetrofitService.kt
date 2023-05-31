package woowacourse.shopping.network.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.entity.ProductEntity

interface ProductRetrofitService {
    @GET("/products")
    fun selectProducts(): Call<List<ProductEntity>>

    @GET("/products/{id}")
    fun selectProduct(
        @Path("id") id: Long
    ): Call<ProductEntity>
}

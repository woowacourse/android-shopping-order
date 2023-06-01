package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.entity.ProductEntity

interface ProductService {
    @GET("products")
    fun requestProducts(): Call<List<ProductEntity>>

    @GET("products/{id}")
    fun requestProduct(@Path("id") id: Int): Call<ProductEntity>
}
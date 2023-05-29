package woowacourse.shopping.data.datasource.retrofit

import com.example.domain.model.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ShoppingCartService {
    @GET("/products")
    fun getProducts(): Call<List<Product>>

    @GET("/products/{productId}")
    fun getProductById(@Path("productId") id: Long): Call<Product>
}

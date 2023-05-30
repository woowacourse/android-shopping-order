package woowacourse.shopping.data.datasource.retrofit

import com.example.domain.model.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductDetailService {
    @GET("/products/{productId}")
    fun getProductById(@Path("productId") id: Long): Call<Product>
}

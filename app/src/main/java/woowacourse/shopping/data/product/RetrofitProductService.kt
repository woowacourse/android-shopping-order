package woowacourse.shopping.data.product

import com.example.domain.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitProductService {

    @GET("/products")
    fun requestFetchAllProducts(): Call<List<Product>>

    @GET("/product/{id}")
    fun requestFetchProductById(@Path("id") id: Long): Call<Product>
}

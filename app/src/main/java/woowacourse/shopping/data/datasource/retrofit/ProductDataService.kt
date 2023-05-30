package woowacourse.shopping.data.datasource.retrofit

import com.example.domain.model.Product
import retrofit2.Call
import retrofit2.http.GET

interface ProductDataService {
    @GET("/products")
    fun getProducts(): Call<List<Product>>
}

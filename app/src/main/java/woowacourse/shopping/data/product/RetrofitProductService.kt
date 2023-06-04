package woowacourse.shopping.data.product

import com.example.domain.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.model.ProductsResponse

interface RetrofitProductService {

    @GET("/products")
    fun requestFetchProductsUnit(
        @Query("unit-size") unitSize: Int,
        @Query("page") page: Int
    ): Call<ProductsResponse>

    @GET("/product/{id}")
    fun requestFetchProductById(@Path("id") id: Long): Call<Product>
}

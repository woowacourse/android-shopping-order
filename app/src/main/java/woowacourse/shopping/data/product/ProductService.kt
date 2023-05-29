import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.product.ProductDataModel

interface ProductService {
    @GET("products")
    fun getAllProducts(): Call<List<ProductDataModel>>

    @GET("product/{id}")
    fun getProductById(
        @Path("id") id: Int,
    ): Call<ProductDataModel>
}

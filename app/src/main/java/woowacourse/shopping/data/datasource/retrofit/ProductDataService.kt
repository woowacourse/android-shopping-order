package woowacourse.shopping.data.datasource.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.remote.request.ProductDTO

interface ProductDataService {
    @GET("products")
    fun getProducts(@Query("limit") limit: Int, @Query("scroll-count") scrollCount: Int): Call<List<ProductDTO>>
}

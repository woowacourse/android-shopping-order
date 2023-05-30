package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.model.DataProduct

interface ProductService {
    @GET("/products")
    fun getAllProducts(): Call<List<DataProduct>>
}

package woowacourse.shopping.data.respository.product.source.remote.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.data.model.ProductEntity

interface ProductService {
    @GET(PATH_PRODUCT)
    fun requestProducts(): Call<List<ProductEntity>>

    @GET("$PATH_PRODUCT/{$PATH_ID}")
    fun requestProductById(@Path(PATH_ID) id: Long): Call<ProductEntity>

    companion object {
        private const val PATH_PRODUCT = "/products"
        private const val PATH_ID = "id"
    }
}

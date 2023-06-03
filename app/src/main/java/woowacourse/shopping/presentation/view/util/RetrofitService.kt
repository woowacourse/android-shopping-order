package woowacourse.shopping.presentation.view.util

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import woowacourse.shopping.presentation.model.ProductModel

interface RetrofitService {
    @GET(PRODUCT)
    fun requestProductsData(): Call<List<ProductModel>>

    @GET("$PRODUCT/{$ID}")
    fun requestDataById(@Path(ID) id: Long): Call<ProductModel>

    companion object {
        private const val PRODUCT = "/products"
        private const val ID = "id"
    }
}

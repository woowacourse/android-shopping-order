package woowacourse.shopping.data.datasource.basket.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.model.DataBasketProduct

interface BasketProductService {

    @GET("cart-items")
    fun requestBasketProducts(
        @Header("Authorization")
        authorization: String
    ): Call<List<DataBasketProduct>>
}

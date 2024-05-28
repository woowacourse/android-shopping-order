package woowacourse.shopping.data.remote.cart

import retrofit2.http.GET

interface RetrofitCartService {
    @GET("")
    fun requestCartItems()
}

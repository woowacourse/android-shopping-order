package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.http.Body
import woowacourse.shopping.data.remote.CartItemIds

interface OrderDataSource {
    fun postOrder(
        cartItemIds: CartItemIds
    ): Call<Unit>
}

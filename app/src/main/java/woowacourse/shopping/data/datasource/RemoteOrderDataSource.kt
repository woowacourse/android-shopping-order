package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.CartItemIds

interface RemoteOrderDataSource {
    fun postOrder(cartItemIds: CartItemIds): Call<Unit>
}

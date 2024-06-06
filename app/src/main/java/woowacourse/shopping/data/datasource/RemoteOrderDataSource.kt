package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.CartItemIds

interface RemoteOrderDataSource {
    suspend fun postOrder(cartItemIds: CartItemIds)
}

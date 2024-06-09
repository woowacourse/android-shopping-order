package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.cart.CartItemIds

interface RemoteOrderDataSource {
    suspend fun postOrder(cartItemIds: CartItemIds)
}

package woowacourse.shopping.data.order.source

import woowacourse.shopping.data.cart.CartItem
import woowacourse.shopping.utils.UserData

class DefaultNetworkOrderDataSource(
    private val orderRemoteService: OrderRemoteService
) : NetworkOrderDataSource {
    override fun loadOrders(): Result<List<NetworkOrder>> {
        return kotlin.runCatching {
            val response = orderRemoteService.requestOrders(
                "Basic ${UserData.credential}"
            ).execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.string())
            response.body() ?: throw Throwable(response.message())
        }
    }

    override fun loadOrder(orderId: Long): Result<NetworkOrder> {
        return kotlin.runCatching {
            val response = orderRemoteService.requestOrder(
                "Basic ${UserData.credential}",
                orderId
            ).execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.string())
            response.body() ?: throw Throwable(response.message())
        }
    }

    override fun saveOrder(cartItems: List<CartItem>): Result<Long> {
        return kotlin.runCatching {
            val response = orderRemoteService.requestToPostOrder(
                "Basic ${UserData.credential}",
                OrderRequestBody(cartItems.map(CartItem::id))
            ).execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.string())
            response.headers()["Location"]
                ?.substringAfterLast("/")
                ?.toLong()
                ?: throw Throwable(response.message())
        }
    }
}

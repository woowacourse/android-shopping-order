package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.ApiCallbackHandler
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.domain.cart.CartsSinglePage

class CartDataSource(
    private val service: CartService,
    private val handler: ApiCallbackHandler,
) {
    fun addCart(
        request: CartItemRequest,
        callback: (Result<String>) -> Unit,
    ) = handler.enqueueWithExtractHeaderLocationResult(service.addCart(request), callback)

    fun singlePage(
        page: Int?,
        size: Int?,
        callback: (Result<CartsSinglePage>) -> Unit,
    ) = handler.enqueueWithDomainTransform(service.getCartSinglePage(page, size), callback)

    fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
        callback: (Result<Unit>) -> Unit,
    ) = handler.enqueueWithResult(service.updateCart(cartId, quantity), callback)

    fun deleteCart(
        cartId: Long,
        callback: (Result<Unit>) -> Unit,
    ) = handler.enqueueWithResult(service.deleteCart(cartId), callback)
}

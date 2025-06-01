package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.enqueueWithExtractHeaderLocationResult
import woowacourse.shopping.data.enqueueWithResult
import woowacourse.shopping.data.enqueueWithTransform
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.domain.cart.CartsSinglePage

class CartDataSource(
    private val service: CartService,
) {
    fun addCart(
        request: CartItemRequest,
        callback: (Result<String?>) -> Unit,
    ) = service.addCart(request).enqueueWithExtractHeaderLocationResult(callback)

    fun singlePage(
        page: Int?,
        size: Int?,
        callback: (Result<CartsSinglePage?>) -> Unit,
    ) = service.getCartSinglePage(page, size).enqueueWithTransform(
        transform = { it.body()?.toDomain() },
        callback = callback,
    )

    fun updateCartQuantity(
        cartId: Long,
        quantity: Int,
        callback: (Result<Unit?>) -> Unit,
    ) {
        service.updateCart(cartId, quantity).enqueueWithResult(callback)
    }

    fun deleteCart(
        cartId: Long,
        callback: (Result<Unit?>) -> Unit,
    ) {
        service.deleteCart(cartId).enqueueWithResult(callback)
    }
}

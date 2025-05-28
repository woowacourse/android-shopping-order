package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class UpdateCartProductUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        productId: Long,
        cartId: Long?,
        quantity: Int,
    ) {
        thread {
            if (cartId == null) {
                repository.addCartProduct(productId, quantity)
            } else {
                repository.updateCartProduct(cartId, quantity)
            }
        }
    }
}

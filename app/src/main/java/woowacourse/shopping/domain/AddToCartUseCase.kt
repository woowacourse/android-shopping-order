package woowacourse.shopping.domain

import woowacourse.shopping.domain.repository.CartRepository

class AddToCartUseCase(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(
        productId: Long,
        quantity: Int = 1,
    ) {
        val cart = cartRepository.cart
        val cartItem = cart.value.items.find { it.product.id == productId }
        if (cartItem != null) {
            cartRepository.changeCartItem(productId, cartItem.increase(quantity).quantity)
        } else {
            cartRepository.addItem(productId, quantity)
        }
    }
}

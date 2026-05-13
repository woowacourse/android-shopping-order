package woowacourse.shopping.presentation.common

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.repository.CartRepository

suspend fun addToCartUseCase(
    cartRepository: CartRepository,
    productId: Long,
    quantity: Int = 1,
): Cart {
    val item = cartRepository.getCart().items.find { it.product.id == productId }
    if (item != null) {
        cartRepository.changeCartItem(productId, item.increase(quantity).quantity)
    } else {
        cartRepository.addItem(productId)
    }
    return cartRepository.getCart()
}

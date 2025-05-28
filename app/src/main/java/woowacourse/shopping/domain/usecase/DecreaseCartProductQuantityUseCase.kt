package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class DecreaseCartProductQuantityUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        product: Product,
        quantityStep: Int = DEFAULT_QUANTITY_STEP,
        callback: (Int) -> Unit = {},
    ) {
        thread {
            if (product.cartId == null) return@thread
            val newQuantity = (product.quantity - quantityStep).coerceAtLeast(MINIMUM_QUANTITY)

            if (newQuantity <= MINIMUM_QUANTITY) {
                repository.deleteCartProduct(product.cartId)
                callback(MINIMUM_QUANTITY)
                return@thread
            } else {
                repository.updateCartProduct(product.cartId, newQuantity)
            }

            callback(newQuantity)
        }
    }

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
    }
}

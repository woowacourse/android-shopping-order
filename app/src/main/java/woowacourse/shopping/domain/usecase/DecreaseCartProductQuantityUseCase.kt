package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import woowacourse.shopping.domain.repository.CartRepository

class DecreaseCartProductQuantityUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        product: Product,
        step: Int = DEFAULT_QUANTITY_STEP,
    ): Int {
        val cartId = product.cartId ?: throw IllegalArgumentException("[DecreaseCartProductQuantityUseCase] 유효하지 않은 상품")

        val newQuantity = (product.quantity - step).coerceAtLeast(MINIMUM_QUANTITY)

        return if (newQuantity <= MINIMUM_QUANTITY) {
            repository.deleteCartProduct(cartId)
            MINIMUM_QUANTITY
        } else {
            repository.updateCartProduct(cartId, newQuantity)
            newQuantity
        }
    }

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
    }
}

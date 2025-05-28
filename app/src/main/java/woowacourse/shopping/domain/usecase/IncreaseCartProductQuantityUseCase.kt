package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class IncreaseCartProductQuantityUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        product: Product,
        quantityStep: Int = DEFAULT_QUANTITY_STEP,
        callback: (Int) -> Unit,
    ) {
        thread {
            val newQuantity = product.quantity + quantityStep

            if (product.cartId == null) {
                repository.addCartProduct(product.productDetail.id, newQuantity)
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

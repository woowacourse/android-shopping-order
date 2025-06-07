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
    ): Result<Int> {
        if (product.cartId == null) return Result.failure(Throwable("[DecreaseCartProductQuantityUseCase] 유효하지 않은 상품"))

        val newQuantity = (product.quantity - step).coerceAtLeast(MINIMUM_QUANTITY)

        return if (newQuantity <= MINIMUM_QUANTITY) {
            deleteCartProduct(product.cartId)
        } else {
            updateCartProduct(product.cartId, newQuantity)
        }
    }

    private suspend fun deleteCartProduct(cartId: Long): Result<Int> =
        repository
            .deleteCartProduct(cartId)
            .map { MINIMUM_QUANTITY }

    private suspend fun updateCartProduct(
        cartId: Long,
        newQuantity: Int,
    ): Result<Int> =
        repository
            .updateCartProduct(cartId, newQuantity)
            .map { newQuantity }

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
    }
}

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
        if (product.cartId == null) {
            return Result.failure(
                IllegalArgumentException(
                    ERROR_PRODUCT_NOT_FOUND_MESSAGE,
                ),
            )
        }
        val newQuantity = (product.quantity - step).coerceAtLeast(MINIMUM_QUANTITY)

        return if (newQuantity <= MINIMUM_QUANTITY) {
            deleteCartProduct(product.cartId)
            Result.success(MINIMUM_QUANTITY)
        } else {
            updateCartProduct(product.cartId, newQuantity)
            Result.success(newQuantity)
        }
    }

    private suspend fun deleteCartProduct(cartId: Long): Result<Unit> = repository.deleteCartProduct(cartId)

    private suspend fun updateCartProduct(
        cartId: Long,
        newQuantity: Int,
    ): Result<Unit> = repository.updateCartProduct(cartId, newQuantity)

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
        private const val ERROR_PRODUCT_NOT_FOUND_MESSAGE = "해당 상품이 존재하지 않습니다."
    }
}

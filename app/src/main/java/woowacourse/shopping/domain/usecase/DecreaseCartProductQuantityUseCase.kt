package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import woowacourse.shopping.domain.repository.CartRepository

class DecreaseCartProductQuantityUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        product: Product,
        step: Int = DEFAULT_QUANTITY_STEP,
        callback: (quantity: Result<Int>) -> Unit = {},
    ) {
        if (product.cartId == null) return
        val newQuantity = (product.quantity - step).coerceAtLeast(MINIMUM_QUANTITY)

        if (newQuantity <= MINIMUM_QUANTITY) {
            deleteCartProduct(product.cartId, callback)
        } else {
            updateCartProduct(product.cartId, newQuantity, callback)
        }
    }

    private fun deleteCartProduct(
        cartId: Long,
        callback: (Result<Int>) -> Unit,
    ) {
        repository.deleteCartProduct(cartId) { result ->
            result
                .onSuccess {
                    callback(Result.success(MINIMUM_QUANTITY))
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun updateCartProduct(
        cartId: Long,
        newQuantity: Int,
        callback: (Result<Int>) -> Unit,
    ) {
        repository.updateCartProduct(cartId, newQuantity) { result ->
            result
                .onSuccess {
                    callback(Result.success(newQuantity))
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
    }
}

package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class IncreaseCartProductQuantityUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        product: Product,
        quantityStep: Int = DEFAULT_QUANTITY_STEP,
        callback: (quantity: Result<Int>) -> Unit = {},
    ) {
        val newQuantity = product.quantity + quantityStep

        if (product.cartId == null) {
            addCartProduct(product, newQuantity, callback)
        } else {
            updateCartProduct(product.cartId, newQuantity, callback)
        }
    }

    private fun addCartProduct(
        product: Product,
        newQuantity: Int,
        callback: (quantity: Result<Int>) -> Unit,
    ) {
        repository.addCartProduct(product.productDetail.id, newQuantity) { result ->
            result
                .onSuccess {
                    callback(Result.success(newQuantity))
                }.onFailure {
                    callback(Result.failure(it))
                }
        }
    }

    private fun updateCartProduct(
        cartId: Long,
        newQuantity: Int,
        callback: (quantity: Result<Int>) -> Unit,
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

package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class IncreaseCartProductQuantityUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        product: Product,
        step: Int = DEFAULT_QUANTITY_STEP,
    ): Int {
        val newQuantity = product.quantity + step

        if (product.cartId == null) {
            addCartProduct(product, newQuantity)
        } else {
            updateCartProduct(product.cartId, newQuantity)
        }

        return newQuantity
    }

    private suspend fun addCartProduct(
        product: Product,
        newQuantity: Int,
    ) {
        repository.addCartProduct(product.productDetail.id, newQuantity)
    }

    private suspend fun updateCartProduct(
        cartId: Long,
        newQuantity: Int,
    ) {
        repository.updateCartProduct(cartId, newQuantity)
    }

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
    }
}

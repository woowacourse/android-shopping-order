package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class IncreaseCartProductQuantityUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        product: Product,
        step: Int = DEFAULT_QUANTITY_STEP,
    ): Result<Int> {
        val newQuantity = product.quantity + step

        return if (product.cartId == null) {
            addCartProduct(product, newQuantity)
            Result.success(newQuantity)
        } else {
            updateCartProduct(product.cartId, newQuantity)
            Result.success(newQuantity)
        }
    }

    private suspend fun addCartProduct(
        product: Product,
        newQuantity: Int,
    ): Result<Unit> = repository.addCartProduct(product.productDetail.id, newQuantity)

    private suspend fun updateCartProduct(
        cartId: Long,
        newQuantity: Int,
    ): Result<Unit> = repository.updateCartProduct(cartId, newQuantity)

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
    }
}

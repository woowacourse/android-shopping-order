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
        } else {
            updateCartProduct(product.cartId, newQuantity)
        }
    }

    private suspend fun addCartProduct(
        product: Product,
        newQuantity: Int,
    ): Result<Int> {
        repository.addCartProduct(product.productDetail.id, newQuantity).getOrElse {
            return Result.failure(Throwable("[IncreaseCartProductQuantityUseCase] 장바구니 상품 추가 오류", it))
        }
        return Result.success(newQuantity)
    }

    private suspend fun updateCartProduct(
        cartId: Long,
        newQuantity: Int,
    ): Result<Int> {
        repository.updateCartProduct(cartId, newQuantity).getOrElse {
            return Result.failure(Throwable("[IncreaseCartProductQuantityUseCase] 장바구니 상품 추가 오류", it))
        }
        return Result.success(newQuantity)
    }

    companion object {
        private const val DEFAULT_QUANTITY_STEP = 1
    }
}

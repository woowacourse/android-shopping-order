package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.domain.model.CartProduct

class GetCartProductByProductIdUseCase(
    private val getPagedCartProductsUseCase: GetPagedCartProductsUseCase,
) {
    suspend operator fun invoke(productId: Int): Result<CartProduct?> =
        getPagedCartProductsUseCase().mapCatching { pagedResult ->
            pagedResult.items.firstOrNull { it.product.id == productId }
        }
}

package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository

class GetCartProductsUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
    ): Result<Products> = repository.fetchCartProducts(page, size)
}

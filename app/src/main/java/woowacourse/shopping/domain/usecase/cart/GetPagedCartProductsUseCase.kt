package woowacourse.shopping.domain.usecase.cart

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartProductRepository

class GetPagedCartProductsUseCase(
    private val cartProductRepository: CartProductRepository,
) {
    suspend operator fun invoke(
        page: Int? = null,
        size: Int? = null,
    ): Result<PagedResult<CartProduct>> = cartProductRepository.getPagedProducts(page, size)
}

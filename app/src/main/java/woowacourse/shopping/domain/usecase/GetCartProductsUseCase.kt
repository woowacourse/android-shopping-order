package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository

class GetCartProductsUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        page: Int,
        size: Int,
        callback: (products: Result<Products>) -> Unit,
    ) {
        repository.fetchCartProducts(page, size) { result ->
            callback(result)
        }
    }
}

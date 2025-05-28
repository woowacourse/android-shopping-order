package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class GetCartProductsUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        page: Int,
        size: Int,
        callback: (Products) -> Unit,
    ) {
        thread {
            callback(repository.fetchCartProducts(page, size))
        }
    }
}

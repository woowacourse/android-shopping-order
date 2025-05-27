package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class GetCartProductsUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(
        page: Int,
        size: Int,
        callback: (CartProducts) -> Unit,
    ) {
        thread {
            callback(repository.fetchCartProducts(page, size))
        }
    }
}

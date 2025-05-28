package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class GetCartProductsQuantityUseCase(
    private val repository: CartRepository,
) {
    operator fun invoke(callback: (quantity: Int) -> Unit) {
        thread {
            callback(repository.fetchCartItemCount())
        }
    }
}

package woowacourse.shopping.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.collections.map

class BuildPaymentItemsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(items: List<OrderItem>): PaymentItems =
        coroutineScope {
            val cartItems =
                items
                    .map { item ->
                        async {
                            CartItem(
                                id = item.cartItemId,
                                product = productRepository.getProductById(item.productId),
                                quantity = item.quantity,
                            )
                        }
                    }.awaitAll()
            PaymentItems(cartItems.toSet())
        }
}

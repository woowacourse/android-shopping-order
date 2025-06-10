package woowacourse.shopping.fixture

import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.repository.OrderRepository

class FakeOrderRepository(
    private val cartProducts: List<CartProduct>,
) : OrderRepository {
    override suspend fun createPaymentSummary(productIds: List<Long>): Result<PaymentSummary> =
        runCatchingDebugLog {
            val cartProducts = cartProducts.filter { productIds.contains(it.product.id) }
            PaymentSummary(cartProducts)
        }

    override suspend fun postOrder(cartProductIds: List<Long>): Result<Unit> = Result.success(Unit)
}

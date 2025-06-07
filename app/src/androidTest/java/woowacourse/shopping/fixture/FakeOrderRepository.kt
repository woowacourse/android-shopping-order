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

    override suspend fun calculatePaymentSummary(
        paymentSummary: PaymentSummary,
        couponId: Long?,
    ): Result<PaymentSummary> =
        runCatchingDebugLog {
            val clearedPaymentSummary = PaymentSummary(paymentSummary.products)
            if (couponId == null) return@runCatchingDebugLog clearedPaymentSummary

            val coupon = couponsFixture.find { it.id == couponId } ?: return@runCatchingDebugLog clearedPaymentSummary
            coupon.calculateDiscountAmount(clearedPaymentSummary)
        }

    override suspend fun postOrder(cartProductIds: List<Long>): Result<Unit> = Result.success(Unit)
}

package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PaymentSummary

interface OrderRepository {
    suspend fun createPaymentSummary(productIds: List<Long>): Result<PaymentSummary>

    suspend fun calculatePaymentSummary(
        paymentSummary: PaymentSummary,
        couponId: Long?,
    ): Result<PaymentSummary>

    suspend fun postOrder(cartProductIds: List<Long>): Result<Unit>
}

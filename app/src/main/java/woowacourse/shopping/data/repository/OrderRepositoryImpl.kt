package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.local.CouponLocalDataSource
import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.data.model.order.OrderRequest
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val cartLocalDataSource: CartLocalDataSource,
    private val couponLocalDataSource: CouponLocalDataSource,
) : OrderRepository {
    override suspend fun createPaymentSummary(productIds: List<Long>): Result<PaymentSummary> =
        runCatchingDebugLog {
            val cartProducts = findCartProducts(productIds)
            PaymentSummary(cartProducts)
        }

    override suspend fun calculatePaymentSummary(
        paymentSummary: PaymentSummary,
        couponId: Long?,
    ): Result<PaymentSummary> =
        runCatchingDebugLog {
            val clearedPaymentSummary = PaymentSummary(paymentSummary.products)
            if (couponId == null) return@runCatchingDebugLog clearedPaymentSummary

            val coupon = requireNotNull(couponLocalDataSource.getCouponById(couponId)) {}
            coupon.calculateDiscountAmount(clearedPaymentSummary)
        }

    override suspend fun postOrder(cartProductIds: List<Long>): Result<Unit> =
        runCatchingDebugLog {
            val orderRequest = OrderRequest(cartProductIds)
            orderRemoteDataSource.postOrder(orderRequest).getOrThrow()
            cartLocalDataSource.removeCartProductsByCartIds(cartProductIds)
        }

    private fun findCartProducts(productIds: List<Long>): List<CartProduct> =
        productIds.mapNotNull { productId -> cartLocalDataSource.getCartProduct(productId) }
}

package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.CartLocalDataSource
import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.data.model.order.OrderRequest
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val cartLocalDataSource: CartLocalDataSource,
) : OrderRepository {
    override suspend fun createPaymentSummary(productIds: List<Long>): Result<PaymentSummary> =
        runCatchingDebugLog {
            val cartProducts = findCartProducts(productIds)
            PaymentSummary(cartProducts)
        }

    override suspend fun postOrder(cartProductIds: List<Long>): Result<Unit> =
        runCatchingDebugLog {
            val orderRequest = OrderRequest(cartProductIds)
            orderRemoteDataSource.postOrder(orderRequest)
            cartLocalDataSource.removeCartProductsByCartIds(cartProductIds)
        }

    private fun findCartProducts(productIds: List<Long>): List<CartProduct> =
        productIds.mapNotNull { productId -> cartLocalDataSource.getCartProduct(productId) }
}

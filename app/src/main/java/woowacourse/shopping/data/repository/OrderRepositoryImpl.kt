package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartLocalDataSource
import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.model.coupon.toTypedDomain
import woowacourse.shopping.data.util.safeApiCall
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val couponRemoteDataSource: CouponRemoteDataSource,
    private val cartLocalDataSource: CartLocalDataSource,
) : OrderRepository {
    override suspend fun addOrder(cartItemIds: List<String>): Result<Unit> =
        safeApiCall {
            cartItemIds.forEach { cartLocalDataSource.deleteCartProductFromCartByCartId(it.toLong()) }
            orderRemoteDataSource.addOrder(cartItemIds)
        }

    override suspend fun fetchOrder(
        cartItemIds: List<Long>,
        couponId: Long?,
    ): Result<Order> =
        safeApiCall {
            Order(
                cartItemIds.map {
                    cartLocalDataSource.fetchCartProductByCartId(it)
                },
                couponRemoteDataSource
                    .fetchCoupons()
                    .find { it.id == couponId }
                    ?.toTypedDomain(),
            )
        }
}

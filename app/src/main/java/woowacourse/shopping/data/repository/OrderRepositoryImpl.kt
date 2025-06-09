package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartLocalDataSource
import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.model.coupon.toTypedDomain
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val couponRemoteDataSource: CouponRemoteDataSource,
    private val cartLocalDataSource: CartLocalDataSource,
) : OrderRepository {
    override suspend fun addOrder(cartItemIds: List<String>): Result<Unit> {
        cartItemIds.forEach { cartLocalDataSource.deleteCartProductFromCartByCartId(it.toLong()) }
        return orderRemoteDataSource.addOrder(cartItemIds)
    }

    override suspend fun fetchOrder(
        cartItemIds: List<Long>,
        couponId: Long?,
    ): Order =
        Order(
            cartItemIds.mapNotNull {
                cartLocalDataSource.findCartProductByCartId(it)
            },
            couponRemoteDataSource
                .fetchCoupons()
                .getOrThrow()
                .find { it.id == couponId }
                ?.toTypedDomain(),
        )
}

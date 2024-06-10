package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.data.datasource.remote.RemoteCouponDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.mapper.toDomain
import java.time.LocalDate
import java.time.LocalTime

class CouponRepositoryImpl(
    private val remoteCouponDataSource: RemoteCouponDataSource,
    private val remoteCartDataSource: RemoteCartDataSource,
) : CouponRepository {
    override suspend fun findCoupons(selectedCartIds: List<Long>): Result<List<Coupon>> {
        return runCatching {
            val carts = findCarts(selectedCartIds.toSet()).getOrThrow()
            when (val result = remoteCouponDataSource.requestCoupons()) {
                is NetworkResult.Success -> {
                    result.data.asSequence()
                        .mapNotNull { it.toDomain(OrderRepository.SHIPPING_FEE, LocalTime.now(), carts) }
                        .filter { it.isValid(LocalDate.now()) }
                        .toList()
                }

                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }

    private suspend fun findCarts(selectedCartIds: Set<Long>): Result<List<Cart>> {
        return runCatching {
            val totalCount =
                when (val result = remoteCartDataSource.getTotalCount()) {
                    is NetworkResult.Success -> result.data.quantity
                    is NetworkResult.Error -> throw result.exception
                }

            val carts =
                when (val cartResult = remoteCartDataSource.getCartItems(0, totalCount)) {
                    is NetworkResult.Success -> {
                        cartResult.data.cartDto.map { it.toCart() }
                    }

                    is NetworkResult.Error -> {
                        return runCatching { throw cartResult.exception }
                    }
                }
            carts.filter { it.cartId in selectedCartIds }
        }
    }
}

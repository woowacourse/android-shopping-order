package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.data.datasource.remote.order.OrderRemoteDataSource
import woowacourse.shopping.mapper.toDomain

class OrderRepositoryImpl(private val orderRemoteDataSource: OrderRemoteDataSource) :
    OrderRepository {

    override fun insertOrderWithCoupon(
        cartItemsIds: List<Long>,
        couponId: Long,
        callback: (Order) -> Unit,
    ) {
        orderRemoteDataSource.postOrderWithCoupon(cartItemsIds, couponId) {
            if (it.isSuccess) {
                val orderDomain = it.getOrNull()?.toDomain()
                callback(orderDomain ?: throw IllegalArgumentException())
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    override fun insertOrderWithoutCoupon(cartItemsIds: List<Long>, callback: (Order) -> Unit) {
        orderRemoteDataSource.postOrderWithoutCoupon(cartItemsIds) {
            if (it.isSuccess) {
                val orderDomain = it.getOrNull()?.toDomain()
                callback(orderDomain ?: throw IllegalArgumentException())
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}

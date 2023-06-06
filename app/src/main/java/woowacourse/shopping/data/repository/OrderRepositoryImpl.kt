package woowacourse.shopping.data.repository

import android.util.Log
import com.example.domain.model.Order
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.data.datasource.remote.order.OrderRemoteDataSource
import woowacourse.shopping.mapper.toDomain

class OrderRepositoryImpl(private val orderRemoteDataSource: OrderRemoteDataSource) : OrderRepository {

    override fun insertOrderWithCoupon(cartItemsIds: List<Long>, couponId: Long): Result<Order> {
        val result = orderRemoteDataSource.postOrderWithCoupon(cartItemsIds, couponId)
        return if (result.isSuccess) {
            val orderDomain = result.getOrNull()?.toDomain()
            Result.success(orderDomain ?: throw IllegalArgumentException())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }

    override fun insertOrderWithoutCoupon(cartItemsIds: List<Long>): Result<Order> {
        val result = orderRemoteDataSource.postOrderWithoutCoupon(cartItemsIds)
        return if (result.isSuccess) {
            val orderDomain = result.getOrNull()?.toDomain()
            Result.success(orderDomain ?: throw IllegalArgumentException())
        } else {
            Log.d("OrderRepositoryImpl", "insertOrderWithoutCoupon: ${result.exceptionOrNull()?.message}")
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }
}

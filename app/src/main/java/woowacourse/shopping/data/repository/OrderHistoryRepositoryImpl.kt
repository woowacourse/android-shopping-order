package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import com.example.domain.repository.OrderHistoryRepository
import woowacourse.shopping.data.datasource.remote.orderhistory.OrderHistorySource
import woowacourse.shopping.mapper.toDomain

class OrderHistoryRepositoryImpl(private val orderHistorySource: OrderHistorySource) :
    OrderHistoryRepository {

    override fun getOrderHistory(): Result<List<Order>> {
        val result = orderHistorySource.getOrderList()
        return if (result.isSuccess) {
            val orderList = result.getOrNull()?.map { it.toDomain() }
            Result.success(orderList ?: throw IllegalArgumentException())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }
}

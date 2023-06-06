package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import com.example.domain.repository.OrderHistoryRepository
import woowacourse.shopping.data.datasource.remote.orderhistory.OrderHistoryRemoteSource
import woowacourse.shopping.mapper.toDomain

class OrderHistoryRepositoryImpl(private val orderHistoryRemoteSource: OrderHistoryRemoteSource) :
    OrderHistoryRepository {
    override fun getOrderHistory(callback: (List<Order>) -> Unit) {
        orderHistoryRemoteSource.getOrderList {
            if (it.isSuccess) {
                val orderList = it.getOrNull()?.map { it.toDomain() }
                callback(orderList ?: throw IllegalArgumentException())
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}

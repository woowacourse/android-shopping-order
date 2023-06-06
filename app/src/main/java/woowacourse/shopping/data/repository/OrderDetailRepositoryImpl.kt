package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import com.example.domain.repository.OrderDetailRepository
import woowacourse.shopping.data.datasource.remote.orderdetail.OrderRemoteDetailSource
import woowacourse.shopping.mapper.toDomain

class OrderDetailRepositoryImpl(private val orderRemoteDetailSource: OrderRemoteDetailSource) :
    OrderDetailRepository {
    override fun getById(id: Long, callback: (Order) -> Unit) {
        orderRemoteDetailSource.getById(id) {
            if (it.isSuccess) {
                val orderDomain = it.getOrNull()?.toDomain()
                callback(orderDomain ?: throw IllegalArgumentException())
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}

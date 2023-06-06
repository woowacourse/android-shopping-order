package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import com.example.domain.repository.OrderDetailRepository
import woowacourse.shopping.data.datasource.remote.orderdetail.OrderRemoteDetailSource
import woowacourse.shopping.mapper.toDomain

class OrderDetailRepositoryImpl(private val orderRemoteDetailSource: OrderRemoteDetailSource) :
    OrderDetailRepository {
    override fun getById(id: Long): Result<Order> {
        val result = orderRemoteDetailSource.getById(id)
        return if (result.isSuccess) {
            val orderDomain = result.getOrNull()?.toDomain()
            Result.success(orderDomain ?: throw IllegalArgumentException())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }
}

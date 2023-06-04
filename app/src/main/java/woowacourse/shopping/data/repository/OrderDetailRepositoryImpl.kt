package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import com.example.domain.repository.OrderDetailRepository
import woowacourse.shopping.data.datasource.remote.orderdetail.OrderDetailSource
import woowacourse.shopping.mapper.toDomain

class OrderDetailRepositoryImpl(private val orderDetailSource: OrderDetailSource) :
    OrderDetailRepository {
    override fun getById(id: Long): Result<Order> {
        val result = orderDetailSource.getById(id)
        return if (result.isSuccess) {
            val orderDomain = result.getOrNull()?.toDomain()
            Result.success(orderDomain ?: throw IllegalArgumentException())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }
}

package woowacourse.shopping.data.repository.impl

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.Order

class OrderRemoteRepository(private val orderDataSource: OrderDataSource) : OrderRepository {
    override fun getAll(callback: (DataResult<List<Order>>) -> Unit) {
        orderDataSource.getAll {
            when (it) {
                is DataResult.Success -> callback(DataResult.Success(it.response.toDomain()))
                is DataResult.Failure -> callback(it)
                is DataResult.NotSuccessfulError -> callback(it)
                is DataResult.WrongResponse -> callback(it)
            }
        }
    }

    override fun getOrder(id: Int, callback: (DataResult<Order>) -> Unit) {
        orderDataSource.getOrder(id) {
            when (it) {
                is DataResult.Success -> callback(DataResult.Success(it.response.toDomain()))
                is DataResult.Failure -> callback(it)
                is DataResult.NotSuccessfulError -> callback(it)
                is DataResult.WrongResponse -> callback(it)
            }
        }
    }

    override fun order(cartProducts: OrderCartItemsDTO, callback: (DataResult<Int?>) -> Unit) {
        orderDataSource.order(cartProducts, callback)
    }
}

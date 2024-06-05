package woowacourse.shopping.data.order

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.datasource.impl.OrderDataSourceImpl
import woowacourse.shopping.data.dto.request.RequestOrderPostDto
import kotlin.concurrent.thread

class OrderRepositoryImpl(private val dataSource: OrderDataSource = OrderDataSourceImpl()) :
    OrderRepository {
    override fun order(cartIds: List<Long>) {
        thread {
            dataSource.postOrder(RequestOrderPostDto(cartIds))
        }.join()
    }
}

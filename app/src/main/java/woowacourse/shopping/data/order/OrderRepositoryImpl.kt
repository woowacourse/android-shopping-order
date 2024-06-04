package woowacourse.shopping.data.order

import woowacourse.shopping.data.dto.request.RequestPostOrderDto
import woowacourse.shopping.data.service.ApiFactory
import kotlin.concurrent.thread

class OrderRepositoryImpl:OrderRepository {
    override fun order(cartIds: List<Long>) {
       thread {
           ApiFactory.postOrder(RequestPostOrderDto(cartIds))
       }.join()
    }
}

package woowacourse.shopping.data

import woowacourse.shopping.data.remote.CartItemIds
import woowacourse.shopping.data.remote.RemoteOrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import kotlin.concurrent.thread

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result = runCatching {
                val response = remoteOrderDataSource.postOrder(CartItemIds(cartItemIds)).execute()
                if (response.isSuccessful) {
                    response.body() ?: throw Exception("No data available")
                } else {
                    throw Exception("Error fetching data")
                }
            }
        }.join()
        return result ?: throw Exception()
    }
}

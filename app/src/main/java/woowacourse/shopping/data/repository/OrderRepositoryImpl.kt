package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.remote.RemoteOrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import kotlin.concurrent.thread

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result =
                runCatching {
                    val response = remoteOrderDataSource.postOrder(OrderRequest(cartItemIds)).execute()
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

package woowacourse.shopping.data.remote.server.repository

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.dto.order.OrderRequest
import woowacourse.shopping.data.remote.server.service.OrderService
import woowacourse.shopping.domain.Order

class OrderRepositoryImpl(
    private val orderService: OrderService
): OrderRepository {
    override suspend fun order(order: Order): ApiResult<Unit> {
        return try {
            val newRequest = OrderRequest(
                ids = order.getAllIds()
            )
            orderService.requestOrder(newRequest)
            ApiResult.Success(Unit)
        } catch (e: HttpException) {
            ApiResult.Error(
                code = e.code(),
                message = e.message
            )
        } catch (e: Exception) {
            ApiResult.Exception(
                e = e
            )
        } catch (e: CancellationException) {
            throw e
        }
    }
}
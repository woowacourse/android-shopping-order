package woowacourse.shopping.data.datasource.remote.order

import woowacourse.shopping.data.datasource.local.AuthInfoLocalDataSource
import woowacourse.shopping.data.datasource.remote.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.OrderDTO
import woowacourse.shopping.data.remote.request.OrderRequestWithCoupon
import woowacourse.shopping.data.remote.request.OrderRequestWithoutCoupon
import java.util.concurrent.Executors

class OrderRemoteDataSourceImpl(private val authInfoLocalDataSource: AuthInfoLocalDataSource) : OrderRemoteDataSource {

    private val token = authInfoLocalDataSource.getAuthInfo() ?: throw IllegalArgumentException()

    override fun postOrderWithCoupon(cartItemsIds: List<Long>, couponId: Long): Result<OrderDTO> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<OrderDTO>> {
            val response =
                ServicePool.orderDataService.postOrderWithCoupon(
                    token,
                    OrderRequestWithCoupon(cartItemsIds, couponId),
                )
                    .execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw IllegalArgumentException())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }

    override fun postOrderWithoutCoupon(cartItemsIds: List<Long>): Result<OrderDTO> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<OrderDTO>> {
            val response =
                ServicePool.orderDataService.postOrderWithoutCoupon(
                    token,
                    OrderRequestWithoutCoupon(cartItemsIds),
                ).execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw IllegalArgumentException())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }
}

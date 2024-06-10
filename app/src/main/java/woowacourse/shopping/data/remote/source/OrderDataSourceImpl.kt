package woowacourse.shopping.data.remote.source

import retrofit2.Response
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.api.OrderApiService
import woowacourse.shopping.data.remote.dto.cart.CartOrderRequest
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.utils.DtoMapper.toDomainModel

class OrderDataSourceImpl(
    private val orderApiService: OrderApiService = NetworkManager.orderService(),
) : OrderDataSource {
    override suspend fun orderItems(ids: List<Long>): Response<Unit> {
        return orderApiService.orderItems(cartItemIds = CartOrderRequest(ids))
    }

    override suspend fun getCoupons(): Response<List<Coupon>> {
        val response = orderApiService.getCoupons()
        return if (response.isSuccessful) {
            Response.success(response.body()?.map { it.toDomainModel() })
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }
}

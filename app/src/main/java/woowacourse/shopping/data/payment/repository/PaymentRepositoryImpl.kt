package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.dto.CouponListResponse
import woowacourse.shopping.data.util.api.ApiResult

class PaymentRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : PaymentRepository {
    override suspend fun fetchAllCoupons(): ApiResult<CouponListResponse> = couponRemoteDataSource.fetchCoupons()

    override suspend fun requestOrder(orderCartIds: List<Int>): ApiResult<Int> = orderRemoteDataSource.requestOrder(orderCartIds)
}

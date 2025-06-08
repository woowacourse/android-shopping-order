package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.OrderRequestResult
import woowacourse.shopping.data.payment.dto.CouponResponse

class PaymentRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : PaymentRepository {
    override suspend fun fetchAllCoupons(): CouponFetchResult<CouponResponse> = couponRemoteDataSource.fetchCoupons()

    override suspend fun requestOrder(orderCartIds: List<Int>): OrderRequestResult<Int> = orderRemoteDataSource.requestOrder(orderCartIds)
}

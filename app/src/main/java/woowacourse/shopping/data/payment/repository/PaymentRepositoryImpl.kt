package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.coupon.dto.CouponResponse
import woowacourse.shopping.data.payment.CouponFetchResult

class PaymentRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : PaymentRepository {
    override suspend fun fetchAllCoupons(): CouponFetchResult<CouponResponse> = couponRemoteDataSource.fetchCoupons()
}

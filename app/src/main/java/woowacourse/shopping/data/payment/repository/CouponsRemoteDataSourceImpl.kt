package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.CouponFetchError
import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.dto.CouponListResponse
import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService

class CouponsRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : CouponRemoteDataSource {
    override suspend fun fetchCoupons(): CouponFetchResult<CouponListResponse> =
        try {
            val response = retrofitService.requestCoupons()
            CouponFetchResult.Success(response)
        } catch (e: Exception) {
            CouponFetchResult.Error(CouponFetchError.Network)
        }
}

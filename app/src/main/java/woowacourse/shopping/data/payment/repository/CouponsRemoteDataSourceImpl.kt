package woowacourse.shopping.data.payment.repository

import woowacourse.shopping.data.payment.dto.CouponListResponse
import woowacourse.shopping.data.util.NetworkModule
import woowacourse.shopping.data.util.RetrofitService
import woowacourse.shopping.data.util.api.ApiError
import woowacourse.shopping.data.util.api.ApiResult

class CouponsRemoteDataSourceImpl(
    private val retrofitService: RetrofitService = NetworkModule.retrofitService,
) : CouponRemoteDataSource {
    override suspend fun fetchCoupons(): ApiResult<CouponListResponse> =
        try {
            val response = retrofitService.requestCoupons()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Network)
        }
}

package woowacourse.shopping.data.coupon.remote.datasource

import woowacourse.shopping.data.common.ApiResponseHandler.handleApiResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.coupon.remote.CouponApiService
import woowacourse.shopping.data.coupon.remote.dto.CouponResponse

class CouponRemoteDataSource(
    private val couponApiService: CouponApiService,
): CouponDataSource {
    override suspend fun loadCoupons(): ResponseResult<CouponResponse> =
        handleApiResponse { couponApiService.requestCoupons() }
}

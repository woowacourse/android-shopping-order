package woowacourse.shopping.data.coupon.remote.datasource

import woowacourse.shopping.data.common.ApiResponseHandler.handleApiResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.coupon.remote.CouponApiService
import woowacourse.shopping.data.coupon.remote.dto.CouponDto

class CouponRemoteDataSource(
    private val couponApiService: CouponApiService,
): CouponDataSource {
    override suspend fun loadCoupons(): ResponseResult<List<CouponDto>> =
        handleApiResponse { couponApiService.requestCoupons() }
}

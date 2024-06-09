package woowacourse.shopping.data.payment.remote.datasource

import woowacourse.shopping.data.common.ResponseHandlingUtils.handleExecute
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.payment.remote.CouponApiService
import woowacourse.shopping.data.payment.remote.dto.CouponResponse

class CouponRemoteDataSource(
    private val couponApiService: CouponApiService,
): CouponDataSource {
    override suspend fun loadCoupons(): ResponseResult<CouponResponse> =
        handleExecute { couponApiService.requestCoupons() }
}

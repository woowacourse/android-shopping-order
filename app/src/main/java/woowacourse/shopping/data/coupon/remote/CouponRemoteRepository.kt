package woowacourse.shopping.data.coupon.remote

import woowacourse.shopping.data.common.ApiResponseHandler.handleResponseResult
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.coupon.remote.datasource.CouponDataSource
import woowacourse.shopping.data.coupon.remote.dto.CouponDto.Companion.toDomain
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.coupon.CouponRepository

class CouponRemoteRepository(
    private val couponDataSource: CouponDataSource
): CouponRepository {
    override suspend fun loadCoupons(): ResponseResult<List<Coupon>> =
        handleResponseResult(couponDataSource.loadCoupons()) { response ->
            val coupons = response.map { it.toDomain() }
            ResponseResult.Success(coupons)
        }
}

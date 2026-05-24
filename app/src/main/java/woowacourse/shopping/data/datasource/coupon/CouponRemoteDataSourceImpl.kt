package woowacourse.shopping.data.datasource.coupon

import woowacourse.shopping.data.remote.api.CouponApi
import woowacourse.shopping.data.remote.dto.CouponResponseDto

class CouponRemoteDataSourceImpl(
    private val couponApi: CouponApi,
) : CouponRemoteDataSource {
    override suspend fun getCoupons(): List<CouponResponseDto> = couponApi.getCoupons()
}

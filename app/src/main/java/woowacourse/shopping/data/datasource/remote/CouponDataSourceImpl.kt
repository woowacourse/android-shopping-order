package woowacourse.shopping.data.datasource.remote

import retrofit2.HttpException
import woowacourse.shopping.data.dto.coupon.CouponResponse
import woowacourse.shopping.data.remote.CouponService

class CouponDataSourceImpl(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun getCoupons(): List<CouponResponse> {
        val response = couponService.requestCoupons()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        throw HttpException(response)
    }
}

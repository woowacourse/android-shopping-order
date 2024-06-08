package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.dto.response.CouponDto

class RemoteCouponDataSource : CouponDataSource {
    override suspend fun getCoupons(): List<CouponDto> = ShoppingRetrofit.couponService.getCoupons().body() ?: emptyList()
}

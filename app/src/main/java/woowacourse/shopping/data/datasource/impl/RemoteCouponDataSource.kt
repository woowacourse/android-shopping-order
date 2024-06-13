package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.response.CouponDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

class RemoteCouponDataSource : CouponDataSource {
    override suspend fun getCoupons(): Result<List<CouponDto>, DataError> =
        handleApi {
            ShoppingRetrofit.couponService.getCoupons()
        }
}

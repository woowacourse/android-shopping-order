package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ApiHandleCouponDataSource
import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.response.CouponDto

class ApiHandleCouponDataSourceImpl : ApiHandleCouponDataSource {
    override suspend fun getCoupons(): ApiResponse<List<CouponDto>> = handleApi {
        ShoppingRetrofit.couponService.getCoupons()
    }
}

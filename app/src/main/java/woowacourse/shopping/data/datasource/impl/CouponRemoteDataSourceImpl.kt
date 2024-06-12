package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.dto.response.ResponseCouponDto
import woowacourse.shopping.data.exception.ShoppingError
import woowacourse.shopping.data.remote.service.CouponService

class CouponRemoteDataSourceImpl(private val service: CouponService) : CouponRemoteDataSource {
    override suspend fun getCoupons(): Result<List<ResponseCouponDto>> =
        runCatching {
            service.getCoupons().body() ?: throw ShoppingError.CouponsNotFound
        }
}

package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.dto.response.ResponseCouponDto
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException

class CouponRemoteDataSourceImpl(private val service: CouponService) : CouponRemoteDataSource {
    override suspend fun getCoupons(): Result<List<ResponseCouponDto>> =
        runCatching {
            service.getCoupons().body() ?: throw ShoppingException(ShoppingError.CouponsNotFound)
        }
}

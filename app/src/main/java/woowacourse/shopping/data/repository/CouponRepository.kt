package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.api.CouponApi
import woowacourse.shopping.data.model.response.CouponResponse.Companion.toDomain
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponDetail.Companion.toCoupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepository(
    private val api: CouponApi,
) : CouponRepository {
    override suspend fun fetchAllCoupons(): Result<List<Coupon>> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getCoupons().mapNotNull { it.toDomain()?.toCoupon() }
            }
        }
}

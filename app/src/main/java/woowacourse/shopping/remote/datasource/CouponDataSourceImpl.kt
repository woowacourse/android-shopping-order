package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.CouponDataSource
import woowacourse.shopping.data.model.remote.CouponDto
import woowacourse.shopping.remote.api.CouponService
import woowacourse.shopping.remote.mapper.toData

class CouponDataSourceImpl(
    private val service: CouponService,
) : CouponDataSource {
    override suspend fun getCoupons(): Result<List<CouponDto>> =
        runCatching {
            service.getCoupons().map { it.toData() }
        }
}

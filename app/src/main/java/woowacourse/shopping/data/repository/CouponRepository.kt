package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.CouponApi
import woowacourse.shopping.data.model.entity.HistoryProductEntity.Companion.toDomain
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepository(
    private val api: CouponApi,
) : CouponRepository {
    override suspend fun fetchAllCopuons(): Result<List<Coupon>> =
        runCatching {
            dao.getHistoryProducts().map { it.toDomain() }
        }
}

package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CouponDetailInfo

interface CouponRepository {
    suspend fun fetchAllCoupons(): Result<List<CouponDetailInfo>>
}

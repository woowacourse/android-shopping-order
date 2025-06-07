package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.Coupon

interface CouponRemoteDataSource {
    suspend fun fetchAll(): Result<List<Coupon>>
}

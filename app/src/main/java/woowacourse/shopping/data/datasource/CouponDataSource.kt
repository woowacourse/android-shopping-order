package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.CouponResponseItem

interface CouponDataSource {
    suspend fun fetchCoupons(): Result<List<CouponResponseItem>>
}

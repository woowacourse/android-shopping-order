package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.coupon.CouponResponseItem

interface RemoteCouponDataSource {
    suspend fun getCoupons(): List<CouponResponseItem>
}

package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.Coupon

interface PayRepository {
    suspend fun getCoupons(): List<Coupon>
}

package woowacourse.shopping.data.remote.datasource.coupon

import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.response.CouponResponse

interface CouponDataSource {
    suspend fun getAll(): Result<Message<List<CouponResponse>>>
}

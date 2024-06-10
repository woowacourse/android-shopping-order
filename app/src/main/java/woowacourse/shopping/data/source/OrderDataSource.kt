package woowacourse.shopping.data.source

import retrofit2.Response
import woowacourse.shopping.domain.model.Coupon

interface OrderDataSource {
    suspend fun orderItems(ids: List<Long>): Response<Unit>

    suspend fun getCoupons(): Response<List<Coupon>>
}

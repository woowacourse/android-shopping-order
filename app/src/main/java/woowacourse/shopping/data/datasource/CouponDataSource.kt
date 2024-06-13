package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.remote.dto.response.CouponDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

interface CouponDataSource {
    suspend fun getCoupons(): Result<List<CouponDto>, DataError>
}

package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.dto.response.CouponResponse

interface RemoteCouponDataSource {
    suspend fun requestCoupons(): NetworkResult<List<CouponResponse>>
}

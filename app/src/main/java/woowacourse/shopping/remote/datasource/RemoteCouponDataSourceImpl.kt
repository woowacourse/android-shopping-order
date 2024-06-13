package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.RemoteCouponDataSource
import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.api.CouponApiService
import woowacourse.shopping.remote.dto.response.CouponResponse
import woowacourse.shopping.remote.executeSafeApiCall

class RemoteCouponDataSourceImpl(private val couponApiService: CouponApiService) : RemoteCouponDataSource {
    override suspend fun requestCoupons(): NetworkResult<List<CouponResponse>> {
        return executeSafeApiCall { couponApiService.requestCoupons() }
    }
}

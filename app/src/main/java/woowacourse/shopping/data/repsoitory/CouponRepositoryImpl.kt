package woowacourse.shopping.data.repsoitory

import android.util.Log
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(private val couponRemoteDataSource: CouponRemoteDataSource) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            couponRemoteDataSource.getCoupons()
        }.onFailure {
            Log.d("LEE", "getCoupons: $it")
        }
}

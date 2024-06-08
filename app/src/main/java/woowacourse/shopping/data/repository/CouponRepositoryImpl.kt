package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toCoupons
import woowacourse.shopping.data.remote.datasource.RemotePaymentDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(private val remotePaymentDataSource: RemotePaymentDataSource) :
    CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        return remotePaymentDataSource.getCoupons().mapCatching { it.toCoupons() }
    }
}

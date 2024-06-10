package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toCouponStates
import woowacourse.shopping.data.remote.datasource.RemotePaymentDataSource
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(private val remotePaymentDataSource: RemotePaymentDataSource) :
    CouponRepository {
    override suspend fun getCouponStates(): Result<List<CouponState>> {
        return remotePaymentDataSource.getCoupons().mapCatching { it.toCouponStates() }
    }
}

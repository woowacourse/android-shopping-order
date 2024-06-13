package woowacourse.shopping.data.repository

import woowacourse.shopping.data.mapper.toCouponStates
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.remote.datasource.RemotePaymentDataSource

class CouponRepositoryImpl(private val remotePaymentDataSource: RemotePaymentDataSource) :
    CouponRepository {
    override suspend fun getCouponStates(): Result<List<CouponState>> {
        return remotePaymentDataSource.getCoupons().mapCatching { it.toCouponStates() }
    }
}

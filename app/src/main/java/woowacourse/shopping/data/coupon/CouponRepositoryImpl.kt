package woowacourse.shopping.data.coupon

import woowacourse.shopping.data.coupon.remote.RemoteCouponDataSource
import woowacourse.shopping.domain.Coupon

class CouponRepositoryImpl(private val remoteCouponDataSource: RemoteCouponDataSource) :
    CouponRepository {
    override suspend fun loadAll(): Result<List<Coupon>> {
        return runCatching {
            remoteCouponDataSource.loadAll()
        }
    }
}

interface CouponRepository {
    suspend fun loadAll(): Result<List<Coupon>>
}

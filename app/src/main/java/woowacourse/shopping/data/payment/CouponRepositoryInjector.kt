package woowacourse.shopping.data.payment

import androidx.annotation.VisibleForTesting
import woowacourse.shopping.data.payment.datasource.CouponDataSourceInjector
import woowacourse.shopping.domain.repository.CouponRepository

object CouponRepositoryInjector {
    @Volatile
    private var instance: CouponRepository? = null

    fun couponRepository(): CouponRepository =
        instance ?: synchronized(this) {
            instance ?: CouponRepositoryImpl(
                CouponDataSourceInjector.couponDataSource(),
            ).also { instance = it }
        }

    @VisibleForTesting
    fun setCouponRepository(couponRepository: CouponRepository) {
        instance = couponRepository
    }

    @VisibleForTesting
    fun clear() {
        instance = null
    }
}

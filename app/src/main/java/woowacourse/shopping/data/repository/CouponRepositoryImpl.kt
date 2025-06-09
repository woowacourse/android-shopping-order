package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.model.coupon.toTypedDomain
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.coupon.Coupons
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalTime

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun fetchFilteredCoupons(
        cartProducts: List<CartProduct>,
        time: LocalTime,
    ): Result<Coupons> =
        couponRemoteDataSource.fetchCoupons().map { coupons ->
            Coupons(coupons.map { coupon -> coupon.toTypedDomain() }).filteredCoupons(
                cartProducts,
                time,
            )
        }
}

package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponRemoteDataSource
import woowacourse.shopping.data.model.coupon.toTypedDomain
import woowacourse.shopping.data.util.safeApiCall
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
        safeApiCall {
            Coupons(
                couponRemoteDataSource.fetchCoupons().map { it.toTypedDomain() },
            ).filteredCoupons(
                cartProducts,
                time,
            )
        }
}

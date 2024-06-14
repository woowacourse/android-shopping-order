package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingCartRemoteDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.coupons.BOGO
import woowacourse.shopping.domain.model.coupons.Coupon
import woowacourse.shopping.domain.model.coupons.FIXED5000
import woowacourse.shopping.domain.model.coupons.FREESHIPPING
import woowacourse.shopping.domain.model.coupons.MIRACLESALE
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalDate
import java.time.LocalTime

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource,
) :
    CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            val cartTotalElement = shoppingCartRemoteDataSource.getCartsTotalElement()
            val carts = shoppingCartRemoteDataSource.getEntireCarts(cartTotalElement).content
            val coupons = couponRemoteDataSource.getCoupons()

            filteredCoupons(carts, coupons)
        }

    private fun filteredCoupons(
        carts: List<Cart>,
        coupons: List<Coupon>,
    ): List<Coupon> {
        val todayDate = LocalDate.now()
        val todayTime = LocalTime.now()
        val totalPrice = carts.sumOf { it.totalPrice }
        val filteredCoupons: MutableList<Coupon> = mutableListOf()

        filteredCoupons.addAll(coupons.filter { it.expirationDate.isAfter(todayDate) })

        return filteredCoupons.filter {
            when (it) {
                is FIXED5000 -> totalPrice >= it.minimumAmount
                is BOGO -> carts.any { cart -> cart.quantity >= 3 }
                is FREESHIPPING -> totalPrice >= it.minimumAmount
                is MIRACLESALE -> todayTime in it.availableTime.start..it.availableTime.end
            }
        }
    }
}

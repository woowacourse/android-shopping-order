package woowacourse.shopping.domain.model.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.view.cart.model.ShoppingCart

class CouponCalculator(
    private val shoppingCart: ShoppingCart,
) {
    private val _discountPrice: MutableLiveData<Int> = MutableLiveData(DEFAULT_PRICE)
    val discountPrice: LiveData<Int> get() = _discountPrice

    fun selectCoupon(coupon: Coupon) {
        when (coupon) {
            Coupon.FIXED5000 -> setCouponFIXED5000()
            Coupon.BOGO -> setCouponBOGO()
            Coupon.FREESHIPPING -> setCouponFREESHIPPING()
            Coupon.MIRACLESALE -> setCouponMIRACLESALE()
        }
    }

    private fun setCouponFIXED5000() {
    }

    private fun setCouponBOGO() {
    }

    private fun setCouponFREESHIPPING() {
    }

    private fun setCouponMIRACLESALE() {
    }

    fun matchCoupon(couponCode: String): Coupon {
        return Coupon.matchCoupon(couponCode)
    }

    companion object {
        const val DEFAULT_PRICE = 0
    }
}

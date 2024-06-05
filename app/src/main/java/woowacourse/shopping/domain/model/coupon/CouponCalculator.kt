package woowacourse.shopping.domain.model.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.view.cart.model.ShoppingCart

class CouponCalculator(
    private val shoppingCart: ShoppingCart,
) {
    private val _discountPrice: MutableLiveData<Int> = MutableLiveData(DEFAULT_PRICE)
    val discountPrice: LiveData<Int> get() = _discountPrice

    fun selectCoupon(couponType: CouponType) {
        when (couponType) {
            CouponType.FIXED5000 -> setCouponFIXED5000()
            CouponType.BOGO -> setCouponBOGO()
            CouponType.FREESHIPPING -> setCouponFREESHIPPING()
            CouponType.MIRACLESALE -> setCouponMIRACLESALE()
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

    companion object {
        const val DEFAULT_PRICE = 0
    }
}

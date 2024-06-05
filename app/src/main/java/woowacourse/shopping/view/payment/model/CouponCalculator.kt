package woowacourse.shopping.view.payment.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponType
import woowacourse.shopping.domain.model.coupon.SelectCouponResult
import woowacourse.shopping.utils.ShoppingUtils.isAvailableTime
import woowacourse.shopping.utils.ShoppingUtils.isPastDate
import woowacourse.shopping.view.cart.model.ShoppingCart

class CouponCalculator {
    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData(emptyList())
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _discountPrice: MutableLiveData<Int> = MutableLiveData(DEFAULT_PRICE)
    val discountPrice: LiveData<Int> get() = _discountPrice

    fun loadCoupons(coupons: List<Coupon>) {
        _coupons.value = coupons
    }

    fun selectCoupon(
        coupon: Coupon,
        shoppingCart: ShoppingCart,
        deliveryCharge: Int,
    ): SelectCouponResult {
        return when (coupon.couponType) {
            CouponType.FIXED5000 -> setCouponFIXED5000(coupon, shoppingCart)
            CouponType.BOGO -> setCouponBOGO(coupon, shoppingCart)
            CouponType.FREESHIPPING -> setCouponFREESHIPPING(coupon, shoppingCart, deliveryCharge)
            CouponType.MIRACLESALE -> setCouponMIRACLESALE(coupon, shoppingCart)
        }
    }

    private fun setCouponFIXED5000(
        coupon: Coupon,
        shoppingCart: ShoppingCart,
    ): SelectCouponResult {
        return if (coupon.isPastDate()) {
            SelectCouponResult.InValidDate
        } else if (shoppingCart.getTotalPrice() >= coupon.minimumAmount && coupon.minimumAmount != DEFAULT_PRICE) {
            selectCoupon(
                discountPrice = coupon.discount,
                coupon = coupon,
            )
            SelectCouponResult.Valid
        } else {
            SelectCouponResult.InValidPrice
        }
    }

    private fun setCouponBOGO(
        coupon: Coupon,
        shoppingCart: ShoppingCart,
    ): SelectCouponResult {
        if (coupon.isPastDate()) {
            SelectCouponResult.InValidDate
        }
        val cartItem =
            shoppingCart.cartItems.value
                ?.filter {
                    it.product.cartItemCounter.itemCount > coupon.buyQuantity + coupon.getQuantity
                }
                ?.maxByOrNull {
                    it.product.price * it.product.cartItemCounter.itemCount
                } ?: return SelectCouponResult.InValidCount
        selectCoupon(
            discountPrice = cartItem.product.price,
            coupon = coupon,
        )
        return SelectCouponResult.Valid
    }

    private fun setCouponFREESHIPPING(
        coupon: Coupon,
        shoppingCart: ShoppingCart,
        deliveryCharge: Int,
    ): SelectCouponResult {
        return if (coupon.isPastDate()) {
            SelectCouponResult.InValidDate
        } else if (shoppingCart.getTotalPrice() >= coupon.minimumAmount && coupon.minimumAmount != DEFAULT_PRICE) {
            selectCoupon(
                discountPrice = deliveryCharge,
                coupon = coupon,
            )
            SelectCouponResult.Valid
        } else {
            SelectCouponResult.InValidCount
        }
    }

    private fun setCouponMIRACLESALE(
        coupon: Coupon,
        shoppingCart: ShoppingCart,
    ): SelectCouponResult {
        return if (coupon.isPastDate() || coupon.isAvailableTime()) {
            SelectCouponResult.InValidDate
        } else {
            val discountPrice = shoppingCart.getTotalPrice() * coupon.discount / 100
            selectCoupon(
                discountPrice = discountPrice,
                coupon = coupon,
            )
            SelectCouponResult.Valid
        }
    }

    private fun selectCoupon(
        coupon: Coupon,
        discountPrice: Int,
    ) {
        coupons.value?.forEach {
            if (it.id == coupon.id) {
                it.itemSelector.selectItem()
            } else {
                it.itemSelector.unSelectItem()
            }
        }
        _discountPrice.value = discountPrice
    }

    companion object {
        const val DEFAULT_PRICE = 0
    }
}

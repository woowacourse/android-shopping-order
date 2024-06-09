package woowacourse.shopping.ui.coupon.uimodel

import android.util.Log
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.response.Fail
import woowacourse.shopping.domain.response.Response
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartError

sealed interface CouponError {
    data object Order : CouponError

    data object LoadCoupon : CouponError

    data object InvalidAuthorized : CouponError

    data object Network : CouponError

    data object UnKnown : CouponError
}

inline fun <reified T : Any?> Response<T>.checkCouponError(execute: (CouponError) -> Unit) = apply {
    when (this) {
        is Response.Success -> {}
        is Fail.InvalidAuthorized -> execute(CouponError.InvalidAuthorized)
        is Fail.Network -> execute(CouponError.Network)
        is Fail.NotFound -> {
            when (T::class) {
                Coupon::class -> execute(CouponError.LoadCoupon)
                Order::class -> execute(CouponError.Order)
                else -> execute(CouponError.UnKnown)
            }
        }

        is Response.Exception -> {
            Log.d(this.javaClass.simpleName, "${this.e}")
            execute(CouponError.UnKnown)
        }
    }
}

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

package woowacourse.shopping.ui.ordercomplete.uimodel

import woowacourse.shopping.model.CartProductUIModel

data class Bill(
    val orderProducts: List<CartProductUIModel>,
    val originPrice: Int,
    val couponName: String?,
    val totalPrice: Int,
) {
    fun showCouponName() = couponName ?: NOTHING

    companion object {
        private const val NOTHING = " "
    }
}

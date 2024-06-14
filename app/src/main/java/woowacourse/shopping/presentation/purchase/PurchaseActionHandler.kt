package woowacourse.shopping.presentation.purchase

interface PurchaseActionHandler {
    fun selectCoupon(
        couponId: Int,
        isChecked: Boolean,
    )
}

package woowacourse.shopping.view.receipt

interface ReceiptActions {
    fun select(couponItem: CouponItem)

    fun unselect()
}
package woowacourse.shopping.view.payment.state

data class CouponUi(
    val title: String,
    val expirationYear: Int,
    val expirationMonth: Int,
    val expirationDay: Int,
    val minimumAmount: Int? = null,
    val checked: Boolean = false,
) {
    fun toggleChecked(): CouponUi {
        return copy(checked = !checked)
    }
}

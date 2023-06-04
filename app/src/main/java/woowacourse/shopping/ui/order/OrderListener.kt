package woowacourse.shopping.ui.order

interface OrderListener {
    fun onOrderClick()
    fun onItemSelected(couponName: String)
}

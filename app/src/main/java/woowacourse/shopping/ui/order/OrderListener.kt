package woowacourse.shopping.ui.order

interface OrderListener {
    fun onOrderClick(id: Int)
    fun onItemSelected(id: Int)
}

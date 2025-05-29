package woowacourse.shopping.view.shoppingCart

interface ShoppingCartClickListener {
    fun onBackButtonClick()

    fun onAllSelectedButtonClick(isChecked: Boolean)

    fun onOrderButtonClick()
}

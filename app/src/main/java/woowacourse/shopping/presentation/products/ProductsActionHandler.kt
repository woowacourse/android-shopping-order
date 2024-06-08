package woowacourse.shopping.presentation.products

interface ProductsActionHandler {
    fun onClickProduct(productId: Int)

    fun onClickLoadMoreButton()

    fun onClickPlusQuantityButton(productId: Int)

    fun onClickMinusQuantityButton(productId: Int)

    fun onClickShoppingCart()
}

package woowacourse.shopping.presentation.products

interface ProductCountActionHandler {
    fun onClickProduct(productId: Int)

    fun onClickPlusQuantityButton(productId: Int)

    fun onClickMinusQuantityButton(productId: Int)
}

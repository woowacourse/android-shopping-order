package woowacourse.shopping.view.productDetail

interface ProductDetailActionsListener {
    fun onClose()

    fun onAddToShoppingCart()

    fun onPlusProductQuantity()

    fun onMinusProductQuantity()

    fun onSelectLatestViewedProduct()
}

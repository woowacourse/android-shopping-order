package woowacourse.shopping.view.cart

interface CartItemActionListener {
    fun onRemoveProduct(item: CartItemType.ProductItem)

    fun onPlusProductQuantity(item: CartItemType.ProductItem)

    fun onMinusProductQuantity(item: CartItemType.ProductItem)

    fun onCheckProduct(item: CartItemType.ProductItem)
}

package woowacourse.shopping.view.product

interface ProductItemActions {
    fun onSelectProduct(item: ProductsItem.ProductItem)

    fun onPlusProductQuantity(item: ProductsItem.ProductItem)

    fun onMinusProductQuantity(item: ProductsItem.ProductItem)
}

package woowacourse.shopping.ui.productList.event

sealed interface ProductListError {
    data object LoadProducts : ProductListError
    data object CalculateFinalPage : ProductListError
    data object LoadProductHistory : ProductListError
    data object LoadCartProducts : ProductListError
    data object AddShoppingCartProduct : ProductListError
    data object UpdateProductQuantity : ProductListError
    data object CountCartProductQuantity : ProductListError
}
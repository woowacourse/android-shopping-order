package woowacourse.shopping.presentation.products

sealed class ProductsNavigateAction {
    class ProductDetailNavigateAction(val productId: Int) : ProductsNavigateAction()

    data object CartNavigateAction : ProductsNavigateAction()
}

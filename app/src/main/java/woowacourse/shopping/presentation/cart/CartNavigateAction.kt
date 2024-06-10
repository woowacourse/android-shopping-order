package woowacourse.shopping.presentation.cart

sealed class CartNavigateAction {
    class ProductDetailNavigateAction(val productId: Int) : CartNavigateAction()

    data object RecommendNavigateAction : CartNavigateAction()
}

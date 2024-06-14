package woowacourse.shopping.presentation.cart

sealed class CartNavigateAction {
    class ProductDetailNavigateAction(val productId: Int) : CartNavigateAction()

    data object RecommendNavigateAction : CartNavigateAction()

    class PurchaseProductNavigateAction(val cartItems: List<Int>) : CartNavigateAction()
}

package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

sealed interface OrderRecommendNavigateAction {
    data object NavigateToProductList : OrderRecommendNavigateAction
}

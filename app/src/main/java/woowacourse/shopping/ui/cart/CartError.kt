package woowacourse.shopping.ui.cart

sealed interface CartError {
    data object UpdateCart : CartError

    data object LoadCart : CartError

    data object LoadRecommend : CartError

}

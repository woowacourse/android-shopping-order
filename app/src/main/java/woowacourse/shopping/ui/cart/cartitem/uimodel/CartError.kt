package woowacourse.shopping.ui.cart.cartitem.uimodel

sealed interface CartError {
    data object UpdateCart : CartError

    data object LoadCart : CartError

    data object LoadRecommend : CartError

    data object InvalidAuthorized : CartError

    data object Network : CartError

    data object UnKnown : CartError
}

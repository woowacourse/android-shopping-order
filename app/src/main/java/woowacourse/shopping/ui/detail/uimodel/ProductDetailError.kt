package woowacourse.shopping.ui.detail.uimodel

sealed interface ProductDetailError {
    data object LoadProduct : ProductDetailError

    data object ChangeCount : ProductDetailError

    data object AddCart : ProductDetailError

    data object InvalidAuthorized : ProductDetailError

    data object Network : ProductDetailError

    data object UnKnown : ProductDetailError
}

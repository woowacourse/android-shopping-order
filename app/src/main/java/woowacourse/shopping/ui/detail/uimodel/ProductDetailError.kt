package woowacourse.shopping.ui.detail.uimodel

sealed interface ProductDetailError {
    data object LoadProduct : ProductDetailError

    data object ChangeCount : ProductDetailError

    data object AddCart : ProductDetailError

    data object Recent:ProductDetailError
}

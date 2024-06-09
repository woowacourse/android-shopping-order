package woowacourse.shopping.ui.products.uimodel

sealed interface ProductListError {
    data object LoadRecentProduct : ProductListError

    data object LoadProduct : ProductListError

    data object AddCart : ProductListError

    data object UpdateCount : ProductListError

    data object InvalidAuthorized : ProductListError

    data object Network : ProductListError

    data object UnKnown : ProductListError
}

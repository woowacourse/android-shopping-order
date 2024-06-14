package woowacourse.shopping.ui.products.uimodel

sealed interface ProductContentError {
    data object RecentProduct : ProductContentError

    data object Product : ProductContentError

    data object Cart : ProductContentError
}

package woowacourse.shopping.ui.productList

sealed interface ProductListUiEvent {
    data class ShowSnackbar(val message: String) : ProductListUiEvent
}

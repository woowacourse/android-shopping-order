package woowacourse.shopping.presentation.product.detail

sealed class CartEvent {
    object AddItemSuccess : CartEvent()

    object AddItemFailure : CartEvent()
}

package woowacourse.shopping.feature.purchase

sealed interface PurchaseUiEvent {
    data class PurchaseComplete(
        val message: String,
    ) : PurchaseUiEvent
}

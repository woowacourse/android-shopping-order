package woowacourse.shopping.ui.event

sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
}

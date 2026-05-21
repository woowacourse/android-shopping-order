package woowacourse.shopping

sealed interface UiEvent {
    data class ShowSnackbar(
        val message: String,
    ) : UiEvent
}

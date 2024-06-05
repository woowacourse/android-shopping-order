package woowacourse.shopping.ui.state

sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()

    data object Loading : UiState<Nothing>()

    data class Error(val exception: Throwable) : UiState<Nothing>()
}

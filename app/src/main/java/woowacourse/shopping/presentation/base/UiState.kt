package woowacourse.shopping.presentation.base

sealed interface UiState<out T : Any> {
    data object Loading : UiState<Nothing>

    data class Success<out T : Any>(val data: T) : UiState<T>
}

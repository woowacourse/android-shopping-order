package woowacourse.shopping.presentation.common

sealed interface UiState<out T : Any> {
    data object Loading : UiState<Nothing>

    data class Success<out T : Any>(val data: T) : UiState<T>
}

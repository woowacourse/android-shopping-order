package woowacourse.shopping.presentation

sealed class UiState<out T> {
    data class Success<out T>(
        val data: T,
    ) : UiState<T>()

    data class Failure(
        val throwable: Throwable? = null,
    ) : UiState<Nothing>()

    data object Loading : UiState<Nothing>()
}

package woowacourse.shopping.ui.util

sealed interface LoadState {
    data object Initial : LoadState

    data object Loading : LoadState

    data object Success : LoadState

    data class Error(
        val throwable: Throwable?,
        val message: String?,
    ) : LoadState
}

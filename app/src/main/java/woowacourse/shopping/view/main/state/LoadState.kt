package woowacourse.shopping.view.main.state

sealed interface LoadState {
    data object CanLoad : LoadState

    data object CannotLoad : LoadState

    companion object {
        fun of(value: Boolean) = if (value) CannotLoad else CanLoad
    }
}

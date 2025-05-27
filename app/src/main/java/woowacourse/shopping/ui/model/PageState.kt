package woowacourse.shopping.ui.model

data class PageState(
    val current: Int = INITIAL_PAGE,
    val total: Int = INITIAL_PAGE,
) {
    val isFirstPage: Boolean
        get() = current == INITIAL_PAGE

    val isLastPage: Boolean
        get() = current == total

    val isSinglePage: Boolean
        get() = total <= INITIAL_PAGE

    operator fun plus(step: Int): PageState = copy(current = current + step)

    operator fun minus(step: Int): PageState = copy(current = current - step)

    companion object {
        const val INITIAL_PAGE: Int = 1
    }
}

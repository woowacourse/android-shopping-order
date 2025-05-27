package woowacourse.shopping.domain.model

data class Page(
    val current: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
) {
    val isSingle: Boolean get() = isFirst == isLast

    operator fun plus(step: Int): Page = copy(current = current + step)

    operator fun minus(step: Int): Page = copy(current = current - step)

    companion object {
        const val UNINITIALIZED_PAGE: Int = -1
        val EMPTY_PAGE = Page(current = UNINITIALIZED_PAGE, isFirst = true, isLast = true)
    }
}

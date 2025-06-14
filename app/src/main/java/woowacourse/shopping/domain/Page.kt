package woowacourse.shopping.domain

data class Page<T>(
    val items: List<T>,
    val isFirst: Boolean,
    val isLast: Boolean,
)

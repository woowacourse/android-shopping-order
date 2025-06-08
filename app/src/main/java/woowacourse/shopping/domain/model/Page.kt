package woowacourse.shopping.domain.model

data class Page<T>(
    val items: List<T>,
    val isFirst: Boolean,
    val isLast: Boolean,
)

package woowacourse.shopping.domain

class Pageable<out I>(
    val items: List<I>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
)

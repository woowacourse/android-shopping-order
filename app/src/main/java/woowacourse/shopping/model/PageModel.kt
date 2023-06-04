package woowacourse.shopping.model

data class PageModel(
    val value: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    override fun toString(): String =
        value.toString()
}

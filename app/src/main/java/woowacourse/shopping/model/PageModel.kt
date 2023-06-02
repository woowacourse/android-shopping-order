package woowacourse.shopping.model

data class PageModel(
    val value: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    fun toText(): String = value.toString()
}

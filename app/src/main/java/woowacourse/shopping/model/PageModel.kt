package woowacourse.shopping.model

data class PageModel(val value: Int) {
    fun toText(): String = value.toString()
}

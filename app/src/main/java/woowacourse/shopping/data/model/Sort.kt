package woowacourse.shopping.data.model

data class Sort(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean,
) {
    companion object {
        val EMPTY =
            Sort(
                sorted = false,
                unsorted = false,
                empty = false,
            )
    }
}

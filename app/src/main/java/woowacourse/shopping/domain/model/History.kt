package woowacourse.shopping.domain.model

data class History(
    val id: Long,
    val name: String,
    val thumbnailUrl: String,
) {
    companion object {
        val EMPTY_HISTORY =
            History(
                id = 0,
                name = "",
                thumbnailUrl = "",
            )
    }
}

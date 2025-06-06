package woowacourse.shopping.data.model

data class ItemCount(
    val quantity: Int,
) {
    companion object {
        val EMPTY =
            ItemCount(
                quantity = 0,
            )
    }
}

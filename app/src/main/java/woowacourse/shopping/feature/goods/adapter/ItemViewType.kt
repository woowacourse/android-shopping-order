package woowacourse.shopping.feature.goods.adapter

enum class ItemViewType(
    val type: Int,
) {
    HISTORY(0),
    DIVIDER(1),
    GOODS(2),
    LOAD_MORE(3),

    ;

    companion object {
        fun from(type: Int): ItemViewType = entries.first { it.type == type }
    }
}

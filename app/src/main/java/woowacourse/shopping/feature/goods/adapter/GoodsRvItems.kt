package woowacourse.shopping.feature.goods.adapter

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.goods.GoodsProduct

sealed class GoodsRvItems(
    val viewType: ItemViewType,
) {
    data class HistoryItem(
        val items: List<Product>,
    ) : GoodsRvItems(ItemViewType.HISTORY)

    data object DividerItem : GoodsRvItems(ItemViewType.DIVIDER)

    data class GoodsItem(
        val item: GoodsProduct,
    ) : GoodsRvItems(ItemViewType.GOODS)

    data object LoadMoreItem : GoodsRvItems(ItemViewType.LOAD_MORE)
}

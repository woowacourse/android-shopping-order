package woowacourse.shopping.feature.goods.adapter

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.feature.goods.GoodsProduct

sealed class ProductFeedItem(
    val viewType: ItemViewType,
) {
    data class HistoryItem(
        val items: List<Product>,
    ) : ProductFeedItem(ItemViewType.HISTORY)

    data object DividerItem : ProductFeedItem(ItemViewType.DIVIDER)

    data class GoodsItem(
        val item: GoodsProduct,
    ) : ProductFeedItem(ItemViewType.GOODS)

    data object LoadMoreItem : ProductFeedItem(ItemViewType.LOAD_MORE)
}

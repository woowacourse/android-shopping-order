package woowacourse.shopping.feature.goods.adapter.vertical

import woowacourse.shopping.domain.model.CartItem

sealed class GoodsListItem {
    data class GoodsData(
        val goodsItem: CartItem,
    ) : GoodsListItem()

    data object Skeleton : GoodsListItem()
}

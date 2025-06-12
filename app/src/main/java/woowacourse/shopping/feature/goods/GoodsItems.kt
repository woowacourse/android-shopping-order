package woowacourse.shopping.feature.goods

import woowacourse.shopping.domain.model.Product

data class GoodsItems(
    val goodsItems: List<GoodsProduct> = emptyList(),
    val historyItems: List<Product> = emptyList(),
    val hasNextPage: Boolean = false,
)

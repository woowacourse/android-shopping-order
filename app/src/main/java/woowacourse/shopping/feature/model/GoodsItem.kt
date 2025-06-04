package woowacourse.shopping.feature.model

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.History

sealed class GoodsItem {
    data class Recent(
        val histories: List<History>,
    ) : GoodsItem()

    data class Product(
        val cart: CartProduct,
    ) : GoodsItem()

    object LoadMore : GoodsItem()
}

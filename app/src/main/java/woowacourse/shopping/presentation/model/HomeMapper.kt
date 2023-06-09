package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.CartProduct

object HomeMapper {
    fun CartProduct.toProductItem(): ProductItem {
        return ProductItem(this)
    }
}

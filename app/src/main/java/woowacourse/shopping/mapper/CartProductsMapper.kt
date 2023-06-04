package woowacourse.shopping.mapper

import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.uimodel.CartProductUIModel

fun CartProducts.toUIModel(): List<CartProductUIModel> {
    return this.all().map { it.toUIModel(this.getCheckedState(it)) }
}

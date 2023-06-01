package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.ui.model.BasketProductUiModel

fun BasketProductUiModel.toBasketProductDomainModel(): BasketProduct =
    BasketProduct(
        id = id,
        count = count.toCountDomainModel(),
        product = product.toProductDomainModel(),
        checked = checked
    )

fun BasketProduct.toBasketProductUiModel(): BasketProductUiModel =
    BasketProductUiModel(id = id, count = count.toCountUiModel(), product = product.toProductUiModel(), checked = checked)

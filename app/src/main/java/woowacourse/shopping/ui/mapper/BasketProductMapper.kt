package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.ui.model.BasketProductUiModel

fun BasketProductUiModel.toDomainModel(): BasketProduct =
    BasketProduct(
        id = id,
        count = count.toDomainModel(),
        product = product.toDomainModel(),
        checked = checked
    )

fun BasketProduct.toUiModel(): BasketProductUiModel =
    BasketProductUiModel(id = id, count = count.toUiModel(), product = product.toUiModel(), checked = checked)

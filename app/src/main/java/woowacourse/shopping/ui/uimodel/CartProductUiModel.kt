package woowacourse.shopping.ui.uimodel

import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts

data class CartProductUiModel(
    val id: Long,
    val productId: Long,
    val quantity: Int,
)

fun PurchaseProduct.toCartProductUiModel(): CartProductUiModel =
    CartProductUiModel(
        id = id,
        productId = productId,
        quantity = count,
    )

fun PurchaseProducts.toCartProductUiModel(): List<CartProductUiModel> =
    purchaseProducts.map { it.toCartProductUiModel() }

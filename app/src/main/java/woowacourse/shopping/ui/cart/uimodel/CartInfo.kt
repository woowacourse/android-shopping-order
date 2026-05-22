package woowacourse.shopping.ui.cart.uimodel

import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.domain.model.PurchaseProducts
import woowacourse.shopping.core.designsystem.component.toPriceString

data class CartInfo(
    val id: Long,
    val productImageUrl: String,
    val productName: String,
    val formattedPrice: String,
    val quantity: Int,
) {
    companion object {
        val PREVIEW =
            CartInfo(
                id = 1L,
                productImageUrl = "",
                productName = "리자몽",
                formattedPrice = "10,000원",
                quantity = 1,
            )
    }
}

fun PurchaseProduct.toUiModel(): CartInfo =
    CartInfo(
        id = id,
        productImageUrl = imageUri,
        productName = name,
        formattedPrice = price.toPriceString(),
        quantity = count,
    )

fun PurchaseProducts.toUiModel(): List<CartInfo> = purchaseProducts.map { it.toUiModel() }

package woowacourse.shopping.presentation.products.adapter.type

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity

data class ProductUiModel(
    val productId: Int,
    val imageUrl: String,
    val title: String,
    val price: Int,
    val quantity: Quantity,
) : ProductsView {
    override val viewType: ProductsViewType = ProductsViewType.PRODUCTS_UI_MODEL

    fun totalPrice() = price * quantity.count

    companion object {
        fun from(
            product: Product,
            quantity: Quantity = Quantity(),
        ): ProductUiModel {
            return ProductUiModel(
                product.id,
                product.imageUrl,
                product.name,
                product.price,
                quantity,
            )
        }
    }
}

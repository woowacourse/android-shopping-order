package woowacourse.shopping.presentation.products.adapter.type

import com.example.domain.model.Product
import com.example.domain.model.Quantity

data class ProductUiModel(
    val productId: Int,
    val imageUrl: String,
    val title: String,
    val price: Int,
    val quantity: com.example.domain.model.Quantity,
) : ProductsView {
    override val viewType: ProductsViewType = ProductsViewType.PRODUCTS_UI_MODEL

    fun totalPrice() = price * quantity.count

    companion object {
        fun from(
            product: com.example.domain.model.Product,
            quantity: com.example.domain.model.Quantity = com.example.domain.model.Quantity(),
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

package woowacourse.shopping.presentation.products.uimodel

import com.example.domain.model.Product
import com.example.domain.model.Quantity

data class ProductUiModel(
    val product: Product,
    val quantity: Quantity,
)
/*
{
    //override val viewType: ProductsViewType = ProductsViewType.PRODUCTS_UI_MODEL

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

 */

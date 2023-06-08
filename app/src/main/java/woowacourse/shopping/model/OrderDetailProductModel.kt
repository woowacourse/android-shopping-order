package woowacourse.shopping.model

import woowacourse.shopping.domain.model.ProductWithQuantity

data class OrderDetailProductModel(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int,
) {
    companion object {
        fun of(product: ProductWithQuantity): OrderDetailProductModel {
            return OrderDetailProductModel(
                product.product.id,
                product.product.name,
                product.product.price.price,
                product.product.imageUrl,
                product.quantity,
            )
        }
    }
}

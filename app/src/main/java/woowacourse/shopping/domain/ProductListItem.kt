package woowacourse.shopping.domain

import java.time.LocalDateTime

sealed interface ProductListItem {
    data class RecentProductItems(val items: List<RecentProductItem>) : ProductListItem

    data class ShoppingProductItem(
        val id: Long,
        val name: String,
        val imgUrl: String,
        val price: Long,
        var quantity: Int = 0,
        val category: String,
        val isChecked: Boolean = false,
    ) : ProductListItem {
        fun toProduct() =
            Product(
                this.id,
                this.name,
                this.imgUrl,
                this.price,
                this.category,
            )

        companion object {
            fun fromProductsAndCarts(
                products: List<Product>,
                carts: List<Cart>,
            ): List<ShoppingProductItem> {
                return products.map { product ->
                    ShoppingProductItem(
                        product.id,
                        product.name,
                        product.imgUrl,
                        product.price,
                        carts.firstOrNull { product == it.product }?.quantity ?: 0,
                        product.category,
                    )
                }
            }

            fun joinProductAndCart(
                product: Product,
                cart: Cart,
            ): ShoppingProductItem =
                ShoppingProductItem(
                    id = product.id,
                    name = product.name,
                    imgUrl = product.imgUrl,
                    price = product.price,
                    quantity = cart.quantity,
                    category = product.category,
                )
        }
    }
}

data class RecentProductItem(
    val productId: Long,
    val name: String,
    val imgUrl: String,
    val dateTime: LocalDateTime,
    val category: String,
)

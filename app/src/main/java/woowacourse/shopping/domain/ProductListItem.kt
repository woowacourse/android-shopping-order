package woowacourse.shopping.domain

import java.time.LocalDateTime

sealed interface ProductListItem {
    data class RecentProductItems(val items: List<RecentProductItem>) : ProductListItem

    data class ShoppingProductItem(
        val cartId: Long = -1,
        val id: Long,
        val name: String,
        val imgUrl: String,
        val price: Long,
        var quantity: Int = 0,
        val isChecked: Boolean = false,
    ) : ProductListItem {
        fun toProduct() =
            Product(
                this.id,
                this.name,
                this.imgUrl,
                this.price,
            )

        companion object {
            fun fromProductsAndCarts(
                products: List<Product>,
                carts: List<Cart>,
            ): List<ShoppingProductItem> {
                return products.map { product ->
                    ShoppingProductItem(
                        carts.firstOrNull { product == it.product }?.cartId ?: -1L,
                        product.id,
                        product.name,
                        product.imgUrl,
                        product.price,
                        carts.firstOrNull { product == it.product }?.quantity ?: 0,
                    )
                }
            }

            fun joinProductAndCart(
                product: Product,
                cart: Cart,
            ): ShoppingProductItem =
                ShoppingProductItem(
                    cart.cartId,
                    product.id,
                    product.name,
                    product.imgUrl,
                    product.price,
                    cart.quantity,
                )
        }
    }
}

data class RecentProductItem(
    val productId: Long,
    val name: String,
    val imgUrl: String,
    val dateTime: LocalDateTime,
)

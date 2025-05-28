package woowacourse.shopping.data.shoppingCart.storage

import woowacourse.shopping.data.ProductsHttpClient
import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.data.shoppingCart.PageableCartItems
import woowacourse.shopping.data.shoppingCart.dto.CartResponse

class RemoteShoppingCartDataSource(
    private val productsHttpClient: ProductsHttpClient = ProductsHttpClient(),
) : ShoppingCartDataSource {
    override fun load(
        page: Int,
        size: Int,
    ): PageableCartItems {
        val cartResponse: CartResponse = productsHttpClient.getCart(page, size)
        val cartItemsEntities: List<CartItemEntity> =
            cartResponse.content?.mapNotNull(CartResponse.Content::toCartItemEntityOrNull)
                ?: emptyList()

        return PageableCartItems(
            cartItems = cartItemsEntities,
            hasPrevious = cartResponse.hasPrevious,
            hasNext = cartResponse.hasNext,
        )
    }

    override fun upsert(cartItem: CartItemEntity) {
        productsHttpClient.postShoppingCartItem(cartItem.id, cartItem.quantity)
    }

    override fun remove(product: CartItemEntity) {
        TODO("Not yet implemented")
    }

    override fun update(products: List<CartItemEntity>) {
        TODO("Not yet implemented")
    }

    override fun quantityOf(productId: Long): Int {
        TODO("Not yet implemented")
    }
}

private fun CartResponse.Content.toCartItemEntityOrNull(): CartItemEntity? =
    if (id == null || product?.name == null || product.price == null || quantity == null) {
        null
    } else {
        CartItemEntity(
            id = id,
            name = product.name,
            price = product.price,
            quantity = quantity,
        )
    }

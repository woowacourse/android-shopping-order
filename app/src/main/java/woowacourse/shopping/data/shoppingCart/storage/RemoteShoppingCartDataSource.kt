package woowacourse.shopping.data.shoppingCart.storage

import woowacourse.shopping.data.ProductsHttpClient
import woowacourse.shopping.data.product.entity.CartItemEntity

class RemoteShoppingCartDataSource(
    private val productsHttpClient: ProductsHttpClient = ProductsHttpClient()
) : ShoppingCartDataSource {
    override fun load(): List<CartItemEntity> {
        TODO("Not yet implemented")
    }

    override fun upsert(cartItem: CartItemEntity) {
        productsHttpClient.postProducts(cartItem.id,cartItem.quantity)
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
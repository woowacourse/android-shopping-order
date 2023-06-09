package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.Product
import java.util.concurrent.CompletableFuture

interface CartItemRepository {

    fun getCartItems(): CompletableFuture<Result<List<CartItem>>>

    fun createCartItem(product: Product, quantity: Int = 1): CompletableFuture<Result<Long>>

    fun updateCartItemQuantity(cartItemId: Long, quantity: Int): CompletableFuture<Result<Unit>>

    fun deleteCartItem(cartItemId: Long): CompletableFuture<Result<Unit>>
}

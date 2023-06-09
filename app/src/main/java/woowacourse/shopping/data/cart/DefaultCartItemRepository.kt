package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.source.NetworkCartItemDataSource
import woowacourse.shopping.data.product.Product
import java.util.concurrent.CompletableFuture

class DefaultCartItemRepository(
    private val networkCartItemDataSource: NetworkCartItemDataSource
) : CartItemRepository {
    override fun getCartItems(): CompletableFuture<Result<List<CartItem>>> {
        return CompletableFuture.supplyAsync {
            networkCartItemDataSource.loadCartItems().mapCatching {
                it.toExternal()
            }
        }
    }

    override fun createCartItem(product: Product, quantity: Int): CompletableFuture<Result<Long>> {
        require(quantity >= 1) { "장바구니 아이템의 수량은 1 이상이어야 합니다." }
        return CompletableFuture.supplyAsync {
            networkCartItemDataSource.saveCartItem(product.id).onSuccess { cartItemId ->
                if (quantity > 1) {
                    networkCartItemDataSource.updateCartItemQuantity(cartItemId, quantity)
                        .onFailure { networkCartItemDataSource.deleteCartItem(cartItemId) }
                }
            }
        }
    }

    override fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int
    ): CompletableFuture<Result<Unit>> {
        require(quantity >= 1) { "장바구니 아이템의 수량은 1 이상이어야 합니다." }
        return CompletableFuture.supplyAsync {
            networkCartItemDataSource.updateCartItemQuantity(cartItemId, quantity)
        }
    }

    override fun deleteCartItem(cartItemId: Long): CompletableFuture<Result<Unit>> {
        return CompletableFuture.supplyAsync {
            networkCartItemDataSource.deleteCartItem(cartItemId)
        }
    }
}

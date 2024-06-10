package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.ui.model.CartItem

class DefaultShoppingCartRepository(
    private val cartSource: ShoppingCartDataSource,
) : ShoppingCartRepository {
    override suspend fun loadAllCartItems(): Result<List<CartItem>> =
        cartSource.loadAllCartItems().mapCatching { cartItemDatas ->
            cartItemDatas.map { cartItemData ->
                CartItem(
                    id = cartItemData.id,
                    product = cartItemData.product.toDomain(),
                    quantity = cartItemData.quantity,
                    checked = false,
                )
            }
        }

    override suspend fun shoppingCartProductQuantity(): Result<Int> =
        cartSource.loadAllCartItems().mapCatching { cartItemDatas -> cartItemDatas.sumOf { it.quantity } }

    override suspend fun updateProductQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> = cartSource.updateProductsCount(cartItemId, quantity)

    override suspend fun addShoppingCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            val cartItem =
                findCartItemByProductId(productId)
                    .map { it }
                    .recover { null }
                    .getOrThrow()

            if (cartItem != null) {
                cartSource.updateProductsCount(cartItem.id, cartItem.quantity + quantity)
            } else {
                cartSource.addNewProduct(ProductIdsCountData(productId, quantity))
            }
        }
    }

    override suspend fun removeShoppingCartProduct(cartItemId: Long): Result<Unit> = cartSource.removeCartItem(cartItemId)

    override suspend fun findCartItemByProductId(productId: Long): Result<CartItem> =
        cartSource.loadAllCartItems().mapCatching { cartItems ->
            cartItems.find { it.product.id == productId }?.toDomain()
                ?: throw NoSuchElementException("There is no product with id: $productId")
        }

    companion object {
        private const val TAG = "DefaultShoppingCartRepository"
    }
}

package woowacourse.shopping.domain.repository

import android.util.Log
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.ui.model.CartItem

class DefaultShoppingCartRepository(
    private val cartSource: ShoppingCartDataSource,
) : ShoppingCartRepository {
    override suspend fun loadAllCartItems2(): Result<List<CartItem>> =
        cartSource.loadAllCartItems2().mapCatching { cartItemDatas ->
            cartItemDatas.map { cartItemData ->
                CartItem(
                    id = cartItemData.id,
                    product = cartItemData.product.toDomain(),
                    quantity = cartItemData.quantity,
                    checked = false,
                )
            }
        }

    override suspend fun shoppingCartProductQuantity2(): Result<Int> =
        cartSource.loadAllCartItems2().mapCatching { cartItemDatas -> cartItemDatas.sumOf { it.quantity } }

    override suspend fun updateProductQuantity2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> = cartSource.updateProductsCount2(cartItemId, quantity)

    override suspend fun addShoppingCartProduct2(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            val cartItem = cartSource.loadAllCartItems2().getOrThrow().find { it.product.id == productId }

            if (cartItem != null) {
                cartSource.updateProductsCount2(cartItem.id, cartItem.quantity + quantity)
            } else {
                cartSource.addNewProduct2(ProductIdsCountData(productId, quantity))
            }
        }
    }

    override suspend fun removeShoppingCartProduct2(cartItemId: Long): Result<Unit> =
        cartSource.removeCartItem2(cartItemId)


    override suspend fun findCartItemByProductId(productId: Long): Result<CartItem> {
        return cartSource.loadAllCartItems2().mapCatching { cartitems ->
            cartitems.find { it.product.id == productId }?.let {
                it.toDomain()
            } ?: throw NoSuchElementException("There is no product with id: $productId")

        }
    }

    companion object {
        private const val TAG = "DefaultShoppingCartRepository"
    }
}

package woowacourse.shopping.data.repository.real

import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.source.CartItemDataSourceImpl
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.DtoMapper.toCartItem
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class RealShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(NetworkManager.getApiClient()),
) : ShoppingCartRepository {
    override suspend fun addCartItem(product: Product): Result<Unit> {
        return cartItemDataSource.addCartItem(
            product.id.toInt(),
            product.cartItemCounter.itemCount,
        )
    }

    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        val page = (offset + 1) / LOAD_SHOPPING_ITEM_SIZE
        val cartItems =
            cartItemDataSource.loadCartItems(page = page, size = pagingSize)
        return cartItems.mapCatching { cartItemResponse ->
            cartItemResponse.cartItemDto.map { it.toCartItem() }
        }
    }

    override suspend fun deleteCartItem(itemId: Long) {
        cartItemDataSource.deleteCartItem(itemId.toInt())
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return cartItemDataSource.loadCartItems()
            .mapCatching { cartItems ->
                val cartItem = cartItems.find { it.product.id == productId }
                CartItemResult(
                    cartItemId = cartItem?.id ?: throw NoSuchDataException(),
                    counter = cartItem.product.cartItemCounter,
                )
            }
            .recoverCatching {
                CartItemResult(
                    cartItemId = DEFAULT_CART_ITEM_ID,
                    counter = CartItemCounter(),
                )
            }
    }

    private suspend fun updateCartCount(cartItemResult: CartItemResult): Result<Unit> {
        return cartItemDataSource.updateCartItem(
            id = cartItemResult.cartItemId.toInt(),
            quantity = cartItemResult.counter.itemCount,
        )
    }

    override suspend fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult> {
        return runCatching {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
            val result = when (updateCartItemType) {
                UpdateCartItemType.INCREASE -> {
                    if (cartItemResult.cartItemId == DEFAULT_CART_ITEM_ID) {
                        increaseItem(cartItemResult, product)
                        addCartItem(product)
                        UpdateCartItemResult.ADD
                    } else {
                        increaseItem(cartItemResult, product)
                        UpdateCartItemResult.UPDATED(cartItemResult)
                    }
                }

                UpdateCartItemType.DECREASE -> {
                    val changeCartItemResult = cartItemResult.decreaseCount()
                    if (changeCartItemResult == ChangeCartItemResultState.Fail) {
                        deleteCartItem(cartItemResult.cartItemId)
                        UpdateCartItemResult.DELETE(cartItemResult.cartItemId)
                    } else {
                        updateCartCount(cartItemResult)
                        UpdateCartItemResult.UPDATED(cartItemResult)
                    }
                }

                is UpdateCartItemType.UPDATE -> {
                    cartItemResult.updateCount(updateCartItemType.count)
                    updateCartCount(cartItemResult)
                    UpdateCartItemResult.UPDATED(cartItemResult)
                }
            }
            result
        }
    }


    private suspend fun increaseItem(
        cartItemResult: CartItemResult,
        product: Product,
    ) {
        cartItemResult.increaseCount()
        product.updateCartItemCount(cartItemResult.counter.itemCount)
        updateCartCount(cartItemResult)
    }

    override suspend fun getTotalCartItemCount(): Result<Int> {
        return runCatching {
            val cartItemCount = cartItemDataSource.loadCartItemCount().getOrThrow()
            cartItemCount.quantity ?: throw NoSuchDataException()
        }
    }

    companion object {
        private const val ERROR_QUANTITY_SIZE = -1
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}

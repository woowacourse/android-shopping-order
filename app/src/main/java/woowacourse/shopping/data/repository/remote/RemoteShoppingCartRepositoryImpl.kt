package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.RemoteCartItemDataSourceImpl
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.cart.CartItemResult
import woowacourse.shopping.domain.model.cart.UpdateCartItemResult
import woowacourse.shopping.domain.model.cart.UpdateCartItemType
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class RemoteShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = RemoteCartItemDataSourceImpl(),
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
        return cartItemDataSource.loadCartItems(page = page, size = pagingSize)
    }

    override suspend fun deleteCartItem(itemId: Long): Result<Unit> {
        return cartItemDataSource.deleteCartItem(itemId.toInt())
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return runCatching {
            val cartItem = cartItemDataSource.loadCartItem(productId).getOrThrow()
            CartItemResult(
                cartItemId = cartItem.id,
                counter = cartItem.product.cartItemCounter,
            )
        }.recoverCatching {
            CartItemResult(DEFAULT_CART_ITEM_ID, CartItemCounter())
        }
    }

    override suspend fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult> {
        return getCartItemResultFromProductId(product.id)
            .mapCatching { cartItemResult ->
                when (updateCartItemType) {
                    UpdateCartItemType.INCREASE -> {
                        if (cartItemResult.cartItemId == DEFAULT_CART_ITEM_ID) {
                            addCartItemResult(cartItemResult, product)
                        } else {
                            increaseItem(cartItemResult, product)
                            updateCartCountResult(cartItemResult)
                        }
                    }

                    UpdateCartItemType.DECREASE -> {
                        val changeCartItemResult = cartItemResult.decreaseCount()
                        if (changeCartItemResult == ChangeCartItemResultState.Fail) {
                            deleteCartItemResult(cartItemResult)
                        } else {
                            updateCartCountResult(cartItemResult)
                        }
                    }

                    is UpdateCartItemType.UPDATE -> {
                        cartItemResult.updateCount(updateCartItemType.count)
                        updateCartCountResult(cartItemResult)
                    }
                }
            }
    }

    private suspend fun addCartItemResult(
        cartItemResult: CartItemResult,
        product: Product,
    ): UpdateCartItemResult {
        increaseItem(cartItemResult, product)
        return addCartItem(product)
            .map {
                UpdateCartItemResult.ADD
            }.getOrElse {
                throw ErrorEvent.UpdateCartEvent()
            }
    }

    private fun increaseItem(
        cartItemResult: CartItemResult,
        product: Product,
    ) {
        cartItemResult.increaseCount()
        product.updateCartItemCount(cartItemResult.counter.itemCount)
    }

    private suspend fun updateCartCountResult(cartItemResult: CartItemResult): UpdateCartItemResult {
        return cartItemDataSource.updateCartItem(
            id = cartItemResult.cartItemId.toInt(),
            quantity = cartItemResult.counter.itemCount,
        )
            .map {
                UpdateCartItemResult.UPDATED(cartItemResult)
            }.getOrElse {
                throw ErrorEvent.UpdateCartEvent()
            }
    }

    private suspend fun deleteCartItemResult(cartItemResult: CartItemResult): UpdateCartItemResult {
        return deleteCartItem(cartItemResult.cartItemId)
            .map {
                UpdateCartItemResult.DELETE(cartItemResult.cartItemId)
            }.getOrElse {
                throw ErrorEvent.DeleteCartEvent()
            }
    }

    override suspend fun getTotalCartItemCount(): Result<Int> {
        return cartItemDataSource.loadCartItemCount()
    }

    companion object {
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}

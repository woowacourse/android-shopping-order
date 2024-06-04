package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.CartItemDataSourceImpl
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class RemoteShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(),
) : ShoppingCartRepository {
    override fun addCartItem(product: Product): Result<Unit> {
        return cartItemDataSource.addCartItem(
            product.id.toInt(),
            product.cartItemCounter.itemCount,
        )
    }

    override fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        val page = (offset + 1) / LOAD_SHOPPING_ITEM_SIZE
        return cartItemDataSource.loadCartItems(page = page, size = pagingSize)
    }

    override fun deleteCartItem(itemId: Long): Result<Unit> {
        return cartItemDataSource.deleteCartItem(itemId.toInt())
    }

    override fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return cartItemDataSource.loadCartItemResult(productId)
    }

    override fun updateCartItem(
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
            .recoverCatching {
                throw ErrorEvent.AddCartEvent()
            }
    }

    private fun addCartItemResult(
        cartItemResult: CartItemResult,
        product: Product,
    ): UpdateCartItemResult {
        increaseItem(cartItemResult, product)
        return addCartItem(product)
            .mapCatching {
                UpdateCartItemResult.ADD
            }
            .recover {
                throw ErrorEvent.AddCartEvent()
            }.getOrThrow()
    }

    private fun increaseItem(
        cartItemResult: CartItemResult,
        product: Product,
    ) {
        cartItemResult.increaseCount()
        product.updateCartItemCount(cartItemResult.counter.itemCount)
    }

    private fun updateCartCountResult(cartItemResult: CartItemResult): UpdateCartItemResult {
        return cartItemDataSource.updateCartItem(
            id = cartItemResult.cartItemId.toInt(),
            quantity = cartItemResult.counter.itemCount,
        )
            .mapCatching {
                UpdateCartItemResult.UPDATED(cartItemResult)
            }
            .recover {
                throw ErrorEvent.UpdateCartEvent()
            }.getOrThrow()
    }

    private fun deleteCartItemResult(cartItemResult: CartItemResult):UpdateCartItemResult {
        return deleteCartItem(cartItemResult.cartItemId)
            .mapCatching {
                UpdateCartItemResult.DELETE(cartItemResult.cartItemId)
            }
            .recover {
                throw ErrorEvent.DeleteCartEvent()
            }.getOrThrow()
    }

    override fun getTotalCartItemCount(): Result<Int> {
        return cartItemDataSource.loadCartItemCount()
    }

    companion object {
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}

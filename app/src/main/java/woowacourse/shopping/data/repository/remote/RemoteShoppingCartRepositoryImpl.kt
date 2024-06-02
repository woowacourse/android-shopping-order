package woowacourse.shopping.data.repository.remote

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
import woowacourse.shopping.utils.DtoMapper.toCartItems
import woowacourse.shopping.utils.DtoMapper.toQuantity
import woowacourse.shopping.utils.exception.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.OrderException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState
import woowacourse.shopping.view.model.event.ErrorEvent

class RemoteShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(),
) : ShoppingCartRepository {

    override fun addCartItem(product: Product) {
        executeWithLatch {
            val response =
                cartItemDataSource.addCartItem(
                    product.id.toInt(),
                    product.cartItemCounter.itemCount,
                ).execute()
            if (!response.isSuccessful) {
                throw OrderException(ErrorEvent.CartEvent.AddCartEvent)
            }
        }
    }

    override fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): List<CartItem> {
        var cartItems: List<CartItem>? = null
        executeWithLatch {
            val page = (offset + 1) / LOAD_SHOPPING_ITEM_SIZE
            val response =
                cartItemDataSource.loadCartItems(page = page, size = pagingSize).execute()
            if (response.isSuccessful) {
                cartItems = response.body()?.toCartItems()
            }
        }
        if (cartItems.isNullOrEmpty()) throw OrderException(ErrorEvent.LoadEvent.MaxPagingDataEvent)
        return cartItems ?: throw OrderException(ErrorEvent.LoadEvent.LoadDataEvent)
    }

    override fun deleteCartItem(itemId: Long) {
        executeWithLatch {
            val response = cartItemDataSource.deleteCartItem(itemId.toInt()).execute()
            if (!response.isSuccessful) {
                throw OrderException(ErrorEvent.CartEvent.DeleteCartEvent)
            }
        }
    }

    override fun getCartItemResultFromProductId(productId: Long): CartItemResult {
        var cartItem: CartItem? = null
        executeWithLatch {
            val response = cartItemDataSource.loadCartItems().execute()
            cartItem =
                response.body()?.toCartItems()?.find { it.product.id == productId }
        }
        return CartItemResult(
            cartItemId = cartItem?.id ?: DEFAULT_CART_ITEM_ID,
            counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
        )
    }

    override fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): UpdateCartItemResult {
        var result: UpdateCartItemResult? = null
        executeWithLatch {
            val cartItemResult = getCartItemResultFromProductId(product.id)
            when (updateCartItemType) {
                UpdateCartItemType.INCREASE -> {
                    result =
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
                    result =
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
                    result = UpdateCartItemResult.UPDATED(cartItemResult)
                }
            }
        }
        return result ?: throw OrderException(ErrorEvent.CartEvent.UpdateCartEvent)
    }

    private fun increaseItem(
        cartItemResult: CartItemResult,
        product: Product,
    ) {
        cartItemResult.increaseCount()
        product.updateCartItemCount(cartItemResult.counter.itemCount)
        updateCartCount(cartItemResult)
    }

    private fun updateCartCount(cartItemResult: CartItemResult) {
        executeWithLatch {
            val response =
                cartItemDataSource.updateCartItem(
                    id = cartItemResult.cartItemId.toInt(),
                    quantity = cartItemResult.counter.itemCount,
                ).execute()
            if (!response.isSuccessful) {
                throw OrderException()
            }
        }
    }

    override fun getTotalCartItemCount(): Int {
        var cartItemCount: Int = ERROR_QUANTITY_SIZE
        executeWithLatch {
            val response = cartItemDataSource.loadCartItemCount().execute()
            if (response.isSuccessful && response.body() != null) {
                cartItemCount = response.body()?.toQuantity() ?: ERROR_QUANTITY_SIZE
            } else {
                throw OrderException(ErrorEvent.LoadEvent.LoadDataEvent)
            }
        }
        if (cartItemCount == ERROR_QUANTITY_SIZE) throw OrderException(ErrorEvent.LoadEvent.LoadDataEvent)
        return cartItemCount
    }

    companion object {
        private const val ERROR_QUANTITY_SIZE = -1
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}

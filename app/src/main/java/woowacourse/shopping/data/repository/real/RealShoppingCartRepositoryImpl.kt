package woowacourse.shopping.data.repository.real

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
import woowacourse.shopping.utils.exception.LatchUtils.awaitOrThrow
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class RealShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(),
) : ShoppingCartRepository {
    private fun executeWithLatch(action: () -> Unit) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null
        thread {
            try {
                action()
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
    }

    override fun addCartItem(product: Product) {
        executeWithLatch {
            val response =
                cartItemDataSource.addCartItem(
                    product.id.toInt(),
                    product.cartItemCounter.itemCount,
                ).execute()
            if (!response.isSuccessful) {
                throw NoSuchDataException()
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
                cartItems = response.body()?.cartItemDto?.map { it.toCartItem() }
            }
        }
        if (cartItems.isNullOrEmpty()) throw NoSuchDataException()
        return cartItems ?: throw NoSuchDataException()
    }

    override fun deleteCartItem(itemId: Long) {
        executeWithLatch {
            val response = cartItemDataSource.deleteCartItem(itemId.toInt()).execute()
            if (!response.isSuccessful) {
                throw NoSuchDataException()
            }
        }
    }

    override fun getCartItemResultFromProductId(productId: Long): CartItemResult {
        var cartItem: CartItem? = null
        executeWithLatch {
            val response = cartItemDataSource.loadCartItems().execute()
            cartItem =
                response.body()?.cartItemDto?.find { it.product.id.toLong() == productId }
                    ?.toCartItem()
        }
        return CartItemResult(
            cartItemId = cartItem?.id ?: DEFAULT_CART_ITEM_ID,
            counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
        )
    }

    private fun updateCartCount(cartItemResult: CartItemResult) {
        executeWithLatch {
            val response =
                cartItemDataSource.updateCartItem(
                    id = cartItemResult.cartItemId.toInt(),
                    quantity = cartItemResult.counter.itemCount,
                ).execute()
            if (!response.isSuccessful) {
                throw NoSuchDataException()
            }
        }
    }

    override fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): UpdateCartItemResult {
        val latch = CountDownLatch(1)
        var result: UpdateCartItemResult? = null
        var exception: Exception? = null
        thread {
            try {
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
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
        return result ?: throw NoSuchDataException()
    }

    private fun increaseItem(
        cartItemResult: CartItemResult,
        product: Product,
    ) {
        cartItemResult.increaseCount()
        product.updateCartItemCount(cartItemResult.counter.itemCount)
        updateCartCount(cartItemResult)
    }

    override fun getTotalCartItemCount(): Int {
        var cartItemCount: Int = ERROR_QUANTITY_SIZE
        executeWithLatch {
            val response = cartItemDataSource.loadCartItemCount().execute()
            if (response.isSuccessful && response.body() != null) {
                cartItemCount = response.body()?.quantity ?: ERROR_QUANTITY_SIZE
            } else {
                throw NoSuchDataException()
            }
        }
        if (cartItemCount == ERROR_QUANTITY_SIZE) throw NoSuchDataException()
        return cartItemCount
    }

    companion object {
        private const val ERROR_QUANTITY_SIZE = -1
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}

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
    override fun addCartItem(product: Product) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        thread {
            try {
                val response =
                    cartItemDataSource.addCartItem(
                        product.id.toInt(),
                        product.cartItemCounter.itemCount,
                    ).execute()
                if (!response.isSuccessful) {
                    exception = NoSuchDataException()
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
    }

    override fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): List<CartItem> {
        val latch = CountDownLatch(1)
        var cartItems: List<CartItem>? = null
        var exception: Exception? = null

        thread {
            try {
                val response =
                    cartItemDataSource.loadCartItems(page = offset, size = pagingSize).execute()
                if (response.isSuccessful) {
                    cartItems = response.body()?.cartItemDto?.map { it.toCartItem() }
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)

        return cartItems ?: throw NoSuchDataException()
    }

    override fun deleteCartItem(itemId: Long) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        thread {
            try {
                val response = cartItemDataSource.deleteCartItem(itemId.toInt()).execute()
                if (!response.isSuccessful) {
                    exception = NoSuchDataException()
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
    }

    override fun getCartItemResultFromProductId(productId: Long): CartItemResult {
        val latch = CountDownLatch(1)
        var cartItem: CartItem? = null
        var exception: Exception? = null
        thread {
            try {
                val response = cartItemDataSource.loadCartItems().execute()
                cartItem =
                    response.body()?.cartItemDto?.find { it.product.id.toLong() == productId }
                        ?.toCartItem()
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
        return CartItemResult(
            cartItemId = cartItem?.id ?: DEFAULT_CART_ITEM_ID,
            counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
        )
    }

    private fun updateCartCount(cartItemResult: CartItemResult) {
        val latch = CountDownLatch(1)
        var exception: Exception? = null
        thread {
            try {
                val response =
                    cartItemDataSource.updateCartItem(
                        id = cartItemResult.cartItemId.toInt(),
                        quantity = cartItemResult.counter.itemCount,
                    ).execute()
                if (!response.isSuccessful) {
                    exception = NoSuchDataException()
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
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
                                UpdateCartItemResult.ADD
                            } else {
                                cartItemResult.increaseCount()
                                product.updateCartItemCount(cartItemResult.counter.itemCount)
                                addCartItem(product)
                                updateCartCount(cartItemResult)
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

    override fun getTotalCartItemCount(): Int {
        val latch = CountDownLatch(1)
        var cartItemCount: Int = ERROR_QUANTITY_SIZE
        var exception: Exception? = null
        thread {
            try {
                val response = cartItemDataSource.loadCartItemCount().execute()
                if (response.isSuccessful && response.body() != null) {
                    cartItemCount = response.body()?.quantity ?: ERROR_QUANTITY_SIZE
                } else {
                    exception = NoSuchDataException()
                }
            } catch (e: Exception) {
                exception = e
            } finally {
                latch.countDown()
            }
        }
        latch.awaitOrThrow(exception)
        if (cartItemCount == ERROR_QUANTITY_SIZE) throw NoSuchDataException()
        return cartItemCount
    }

    companion object {
        private const val ERROR_QUANTITY_SIZE = -1
    }
}

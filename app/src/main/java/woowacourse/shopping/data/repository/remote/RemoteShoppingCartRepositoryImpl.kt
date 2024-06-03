package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.CartItemDataSourceImpl
import woowacourse.shopping.data.source.CartItemDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.DtoMapper.toCartItem
import woowacourse.shopping.utils.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.cartcounter.ChangeCartItemResultState

class RemoteShoppingCartRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = CartItemDataSourceImpl(),
) : ShoppingCartRepository {
    override fun addCartItem(product: Product): Result<Unit> {
        return executeWithLatch {
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
    ): Result<List<CartItem>> {
        return executeWithLatch {
            val page = (offset + 1) / LOAD_SHOPPING_ITEM_SIZE
            val response = cartItemDataSource.loadCartItems(page = page, size = pagingSize).execute()
            if (response.isSuccessful) {
                response.body()?.cartItemDto?.map { it.toCartItem() } ?: emptyList()
            } else {
                throw NoSuchDataException()
            }
        }
    }

    override fun deleteCartItem(itemId: Long): Result<Unit> {
        return executeWithLatch {
            val response = cartItemDataSource.deleteCartItem(itemId.toInt()).execute()
            if (!response.isSuccessful) {
                throw NoSuchDataException()
            }
        }
    }

    override fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return executeWithLatch {
            val response = cartItemDataSource.loadCartItems().execute()
            val cartItem = response.body()?.cartItemDto?.find { it.product.id.toLong() == productId }?.toCartItem()
            CartItemResult(
                cartItemId = cartItem?.id ?: CartItem.DEFAULT_CART_ITEM_ID,
                counter = cartItem?.product?.cartItemCounter ?: CartItemCounter(),
            )
        }
    }

    private fun updateCartCount(cartItemResult: CartItemResult): Result<Unit> {
        return executeWithLatch {
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
    ): Result<UpdateCartItemResult> {
        return executeWithLatch {
            val cartItemResult = getCartItemResultFromProductId(product.id).getOrThrow()
            when (updateCartItemType) {
                UpdateCartItemType.INCREASE -> {
                    if (cartItemResult.cartItemId == CartItem.DEFAULT_CART_ITEM_ID) {
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
        }
    }

    private fun increaseItem(
        cartItemResult: CartItemResult,
        product: Product,
    ) {
        cartItemResult.increaseCount()
        product.updateCartItemCount(cartItemResult.counter.itemCount)
        updateCartCount(cartItemResult)
    }

    override fun getTotalCartItemCount(): Result<Int> {
        return executeWithLatch {
            val response = cartItemDataSource.loadCartItemCount().execute()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.quantity ?: ERROR_QUANTITY_SIZE
            } else {
                throw NoSuchDataException()
            }
        }.mapCatching {
            if (it == ERROR_QUANTITY_SIZE) throw NoSuchDataException()
            it
        }
    }

    companion object {
        private const val ERROR_QUANTITY_SIZE = -1
        const val LOAD_SHOPPING_ITEM_SIZE = 50
        const val LOAD_SHOPPING_ITEM_OFFSET = 0
        const val LOAD_RECOMMEND_ITEM_SIZE = 10
    }
}

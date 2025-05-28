package woowacourse.shopping.data.repository.remote

import android.util.Log
import woowacourse.shopping.data.datasource.remote.CartDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartDataSource: CartDataSource,
) : CartRepository {
    private var cachedCart: Cart = Cart()

    init {
        fetchAllCartItems()
    }

    override fun getTotalCount(onResult: (Result<Int>) -> Unit) {
        cartDataSource.getTotalCount { result ->
            onResult(result)
        }
    }

    override fun fetchPagedCartItems(
        page: Int,
        pageSize: Int,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        cartDataSource.getPagedCartItems(page, pageSize) { cartItems ->
            onResult(Result.success(cartItems))
        }
    }

    override fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        if (cachedCart.exist(productId = product.productId)) {
            val cartItem =
                cachedCart.findCartItem(product.productId) ?: throw NoSuchElementException("")

            updateProduct(cartItem.cartId, cartItem.quantity + productQuantity) {
                onResult(Result.success(Unit))
            }
        } else {
            insertProduct(productId = product.productId, productQuantity) { result ->
                val cartId = result.getOrNull() ?: -1L
                val cartItem =
                    CartItem(cartId = cartId, product = product, quantity = productQuantity)
                cachedCart = cachedCart.add(cartItem)
                onResult(Result.success(Unit))
            }
        }
    }

    override fun insertProduct(
        productId: Long,
        productQuantity: Int,
        onResult: (Result<Long>) -> Unit,
    ) {
        cartDataSource.insertCartItem(productId, productQuantity) { result ->
            onResult(result)
        }
    }

    override fun updateProduct(
        cartId: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartDataSource.updateQuantity(cartId, quantity) { result ->
            onResult(result)
        }
    }

    override fun deleteProduct(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartId = cachedCart.findCartItem(productId)?.cartId ?: -1
        cartDataSource.deleteCartItemById(cartId) { result ->
            onResult(result)
        }
    }

    private fun fetchAllCartItems() {
        cartDataSource.getTotalCount { result ->
            val totalCount = result.getOrNull() ?: 0

            cartDataSource.getPagedCartItems(0, totalCount) { cartItems ->
                cachedCart = Cart(cartItems)
                Log.d("meeple_log", "$cachedCart")
            }
        }
    }
}

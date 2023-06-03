package woowacourse.shopping.data.repositoryImpl

import java.lang.Integer.min
import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.model.CartProducts

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource
) : CartRepository {
    private val cartItems = CartProducts(mutableListOf())

    override fun getAll(callback: (Result<CartProducts>) -> Unit) {
        remoteDataSource.getAll { result ->
            result.onSuccess { cartProducts ->
                cartItems.replaceAll(cartProducts)
                callback(Result.success(cartItems))
            }.onFailure { throwable -> callback(Result.failure(throwable)) }
        }
    }

    override fun getPage(index: Int, size: Int, callback: (Result<CartProducts>) -> Unit) {
        remoteDataSource.getAll { result ->
            result.onSuccess {
                val cartProducts = it.subList(index * size, min((index + 1) * size, it.size))
                cartItems.replaceAll(it)
                callback(Result.success(CartProducts(cartProducts.toMutableList())))
            }.onFailure { throwable -> callback(Result.failure(throwable)) }
        }
    }

    override fun hasNextPage(index: Int, size: Int): Boolean {
        return index < (cartItems.size - 1) / size
    }

    override fun hasPrevPage(index: Int, size: Int): Boolean {
        return index > 0
    }

    override fun getTotalCount(): Int {
        return cartItems.totalQuantity
    }

    override fun getTotalSelectedCount(): Int {
        return cartItems.totalCheckedQuantity
    }

    override fun getTotalPrice(): Int {
        return cartItems.totalPrice
    }

    override fun insert(productId: Int) {
        if (cartItems.findByProductId(productId) != null) {
            return
        }
        remoteDataSource.postItem(productId) { getAll {} }
    }

    override fun remove(id: Int, callback: () -> Unit) {
        remoteDataSource.deleteItem(id) { callback() }
    }

    override fun updateCountWithProductId(
        productId: Int,
        count: Int,
        callback: (Result<Int>) -> Unit
    ) {
        if (cartItems.isEmpty()) {
            remoteDataSource.getAll { result ->
                result.onSuccess { cartProducts -> cartItems.replaceAll(cartProducts) }
                    .onFailure { throwable -> callback(Result.failure(throwable)) }
            }
        }
        val cartItem = cartItems.findByProductId(productId)
        when {
            cartItem == null && count == 1 -> remoteDataSource.postItem(productId, callback)
            cartItem == null -> return
            count == 0 -> remoteDataSource.deleteItem(cartItem.id, callback)
            count < 1 -> return
            else -> remoteDataSource.patchItemQuantity(cartItem.id, count, callback)
        }
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        cartItems.changeChecked(id, checked)
    }
}

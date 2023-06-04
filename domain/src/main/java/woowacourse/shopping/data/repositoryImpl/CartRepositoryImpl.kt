package woowacourse.shopping.data.repositoryImpl

import java.lang.Integer.min
import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.model.CartProducts

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource
) : CartRepository {
    private val cartItems = CartProducts(mutableListOf())

    override fun getAll(): Result<CartProducts> {
        val result = remoteDataSource.getAll()
        result.onSuccess {
            cartItems.replaceAll(it)
            return Result.success(CartProducts(it.toMutableList()))
        }
        return Result.failure(Throwable("Failed to get all"))
    }

    override fun getPage(index: Int, size: Int): Result<CartProducts> {
        remoteDataSource.getAll()
            .onSuccess { cartProducts ->
                val cartItems = cartProducts.subList(
                    index * size,
                    min((index + 1) * size, cartProducts.size)
                )
                this.cartItems.replaceAll(cartProducts)
                return Result.success(CartProducts(cartItems.toMutableList()))
            }.onFailure { throwable ->
                return Result.failure(throwable)
            }
        return Result.failure(Throwable("Failed to get page"))
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

    override fun insert(productId: Int): Result<Int> {
        if (cartItems.firstOrNull { it.product.id == productId } != null) {
            return Result.failure(Throwable("Already exist"))
        }
        return remoteDataSource.postItem(productId)
    }

    override fun remove(id: Int): Result<Int> {
        return remoteDataSource.deleteItem(id)
    }

    override fun updateCountWithProductId(productId: Int, count: Int): Result<Int> {
        if (cartItems.isEmpty()) {
            remoteDataSource.getAll()
                .onSuccess { cartItems.replaceAll(it) }
                .onFailure { throwable -> return Result.failure(throwable) }
        }
        val cartItem = cartItems.firstOrNull { it.product.id == productId }
        return when {
            cartItem == null && count == 1 -> remoteDataSource.postItem(productId)
            cartItem == null -> Result.success(-1)
            count == 0 -> remoteDataSource.deleteItem(cartItem.id)
            count < 1 -> Result.success(-1)
            else -> remoteDataSource.patchItemQuantity(cartItem.id, count)
        }
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        cartItems.setCheck(id, checked)
    }
}

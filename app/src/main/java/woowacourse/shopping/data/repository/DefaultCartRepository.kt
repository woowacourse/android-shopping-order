package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.local.cart.CartDao
import woowacourse.shopping.data.source.remote.CartRemoteDataSource
import woowacourse.shopping.domain.model.AddItemResult
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val cartDao: CartDao,
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override suspend fun getCart(): Cart {
        val rows = remoteDataSource.getCartItems(0, 10000)

        val items =
            rows.map {
                CartItem(
                    Product(
                        id = it.product.id,
                        name =
                            ProductName(
                                it.product.name,
                            ),
                        price =
                            Money(
                                it.product.price.toLong(),
                            ),
                        imageUrl = it.product.imageUrl,
                    ),
                    it.quantity,
                )
            }

        return Cart(items)
    }

    override suspend fun addItem(
        id: Long,
        quantity: Int,
    ): AddItemResult {
        remoteDataSource.addItem(
            id = id,
            quantity = quantity,
        )
        val before = cartDao.findById(id)
        cartDao.addOrIncrement(id, quantity)
        val cart = getCart()
        return if (before == null) {
            AddItemResult.NewAdded(cart)
        } else {
            AddItemResult.Incremented(cart)
        }
    }

    override suspend fun getTotalCartSize(): Int = cartDao.getTotalCartSize()

    override suspend fun deleteItem(id: Long): RemoveItemResult {
        val isDeleted = cartDao.deleteItem(id)
        return if (isDeleted > 0) {
            RemoveItemResult.Success(getCart())
        } else {
            RemoveItemResult.NotFoundItem
        }
    }

    override suspend fun decrease(id: Long): RemoveItemResult {
        val existing = cartDao.findById(id) ?: return RemoveItemResult.NotFoundItem
        cartDao.deleteOrDecrement(existing.productId)
        return RemoveItemResult.Success(getCart())
    }

    override suspend fun getAllQuantities(): Map<Long, Int> = cartDao.getAll().associate { it.productId to it.quantity }

    override suspend fun getQuantity(id: Long): Int = cartDao.findById(id)?.quantity ?: 1
}

package woowacourse.shopping.repository.room

import woowacourse.shopping._archive.local.dao.CartDao
import woowacourse.shopping._archive.local.entity.CartEntity
import woowacourse.shopping.model.Cart
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository

class RoomCartRepository(
    private val cartDao: CartDao,
    private val productRepository: ProductRepository,
) : CartRepository {
    override suspend fun getAllCartItems(): Cart {
        val cartItems = toCartItems()
        return Cart(cartItems)
    }

    override suspend fun add(
        item: Product,
        quantity: Int,
    ) {
        val currentEntity = cartDao.getCartItemById(item.id)
        if (currentEntity != null) {
            cartDao.updateQuantity(item.id, currentEntity.quantity + quantity)
        } else {
            cartDao.insert(CartEntity(item.id, quantity))
        }
    }

    override suspend fun increase(item: Product) {
        val currentEntity = cartDao.getCartItemById(item.id)

        if (currentEntity != null) {
            cartDao.updateQuantity(productId = item.id, currentEntity.quantity + 1)
        } else {
            cartDao.insert(CartEntity(item.id, 1))
        }
    }

    override suspend fun decrease(item: Product) {
        val currentEntity = cartDao.getCartItemById(item.id)

        if (currentEntity != null) {
            if (currentEntity.quantity > 1) {
                cartDao.updateQuantity(item.id, currentEntity.quantity - 1)
            } else {
                cartDao.deleteById(item.id)
            }
        }
    }

    override suspend fun delete(item: Product) {
        cartDao.deleteById(item.id)
    }

    override suspend fun getPagedItems(
        page: Int,
        count: Int,
    ): List<CartItem> {
        require(page >= 0) { "$page 는 0 이상의 정수여야 합니다." }
        require(count >= 0) { "count는 0 이상의 정수여야 합니다." }

        val pagedEntities = cartDao.getPagedEntities(page, count)

        return pagedEntities.mapNotNull { entity ->
            val product = productRepository.findProduct(entity.productId)

            if (product != null) {
                CartItem(product = product, quantity = entity.quantity)
            } else {
                cartDao.deleteById(entity.productId)
                null
            }
        }
    }

    override suspend fun getSize(): Int = cartDao.getSize()
    override suspend fun getCartCount(): Int {
        TODO("Not yet implemented")
    }

    private suspend fun toCartItems(): List<CartItem> {
        val cartEntities = cartDao.getAll()

        val items =
            cartEntities.mapNotNull { entity ->
                val product = productRepository.findProduct(entity.productId)

                if (product != null) {
                    CartItem(product = product, quantity = entity.quantity)
                } else {
                    cartDao.deleteById(entity.productId)
                    null
                }
            }
        return items
    }
}

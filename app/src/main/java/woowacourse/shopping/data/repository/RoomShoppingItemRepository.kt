package woowacourse.shopping.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.data.local.room.shoppingItem.ShoppingItemDao
import woowacourse.shopping.data.local.room.shoppingItem.ShoppingItemEntity
import woowacourse.shopping.data.local.room.shoppingItem.toDomain
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class RoomShoppingItemRepository(
    private val shoppingItemDao: ShoppingItemDao,
    scope: CoroutineScope,
) : ShoppingItemRepository {
    override val shoppingItems: StateFlow<List<ShoppingItem>> =
        shoppingItemDao
            .observeAll()
            .map { entities -> entities.map { entity -> entity.toDomain() } }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun upsertProduct(product: Product) {
        val preservedQuantity = shoppingItemDao.getQuantityOrNull(product.id) ?: 0
        shoppingItemDao.insertAll(
            listOf(
                ShoppingItemEntity(
                    productId = product.id,
                    title = product.getTitle(),
                    price = product.getPrice(),
                    imageUrl = product.imageUrl,
                    category = product.category,
                    quantity = preservedQuantity,
                ),
            ),
        )
    }

    override suspend fun replaceProducts(products: List<Product>) {
        if (products.isEmpty()) {
            shoppingItemDao.deleteAll()
            return
        }
        val quantityByProductId =
            shoppingItemDao
                .getAll()
                .associate { entity -> entity.productId to entity.quantity }
        val entities =
            products.map { product ->
                ShoppingItemEntity(
                    productId = product.id,
                    title = product.getTitle(),
                    price = product.getPrice(),
                    imageUrl = product.imageUrl,
                    category = product.category,
                    quantity = quantityByProductId[product.id] ?: 0,
                )
            }
        shoppingItemDao.insertAll(entities)
        shoppingItemDao.deleteByProductIdsNotIn(entities.map { entity -> entity.productId })
    }

    override suspend fun getQuantity(productId: Long): Int =
        shoppingItemDao.getQuantityOrNull(productId)
            ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")

    override suspend fun plusQuantity(
        productId: Long,
        amount: Int,
    ) {
        if (amount == 0) return
        updateQuantityByDelta(productId, amount)
    }

    override suspend fun minusQuantity(
        productId: Long,
        amount: Int,
    ) {
        if (amount == 0) return
        updateQuantityByDelta(productId, -amount)
    }

    private suspend fun updateQuantityByDelta(
        productId: Long,
        delta: Int,
    ) {
        val currentQuantity =
            shoppingItemDao.getQuantityOrNull(productId)
                ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")
        val updatedQuantity = (currentQuantity + delta).coerceAtLeast(0)
        shoppingItemDao.updateQuantity(productId, updatedQuantity)
    }
}

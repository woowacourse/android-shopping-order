package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartLocalDataSource
import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.db.CartEntity
import woowacourse.shopping.data.model.toProduct
import woowacourse.shopping.data.util.runCatchingInThread
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val cartLocalDataSource: CartLocalDataSource,
    private val productRemoteDataSource: ProductRemoteDataSource,
) : CartRepository {
    override fun getAll(onResult: (Result<List<CartProduct>>) -> Unit) =
        runCatchingInThread(onResult) {
            cartLocalDataSource.getAll().toCartItems()
        }

    override fun getTotalQuantity(onResult: (Result<Int>) -> Unit) =
        runCatchingInThread(onResult) {
            cartLocalDataSource.getTotalQuantity()
        }

    override fun loadCartItems(
        offset: Int,
        limit: Int,
        onResult: (Result<PageableItem<CartProduct>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val entities = cartLocalDataSource.loadCartItems(offset, limit)
        val cartItems = entities.toCartItems()
        val hasMore = entities.hasMore()
        PageableItem(cartItems, hasMore)
    }

    override fun findCartItemByProductId(
        productId: Long,
        onResult: (Result<CartProduct>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartLocalDataSource.findCartItemByProductId(productId).toCartItem()
    }

    override fun findQuantityByProductId(
        productId: Long,
        onResult: (Result<Int>) -> Unit,
    ) = runCatchingInThread(onResult) {
        runCatching { cartLocalDataSource.findCartItemByProductId(productId).quantity }.getOrDefault(0)
    }

    override fun addCartItem(
        productId: Long,
        increaseQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartLocalDataSource.addCartItem(productId, increaseQuantity)
    }

    override fun decreaseCartItemQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartLocalDataSource.decreaseCartItemQuantity(productId)
    }

    override fun deleteCartItem(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) = runCatchingInThread(onResult) {
        cartLocalDataSource.deleteCartItem(productId)
    }

    private fun CartEntity.toCartItem(): CartProduct {
        val product = productRemoteDataSource.findProductById(productId).toProduct()
        return CartProduct(product, quantity)
    }

    private fun List<CartEntity>.toCartItems(): List<CartProduct> {
        val productIds = this.map { it.productId }
        val productMap = productRemoteDataSource.findProductsByIds(productIds).associateBy { it.id }

        return this.mapNotNull { entity ->
            val product = productMap[entity.productId]?.toProduct() ?: return@mapNotNull null
            CartProduct(product, entity.quantity)
        }
    }

    private fun List<CartEntity>.hasMore(): Boolean {
        val lastCreatedAt = this.lastOrNull()?.createdAt
        return lastCreatedAt != null && cartLocalDataSource.existsItemCreatedAfter(lastCreatedAt)
    }
}

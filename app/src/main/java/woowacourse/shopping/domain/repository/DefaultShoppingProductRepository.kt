package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartProductIdDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.CartItemDto

class DefaultShoppingProductRepository(
    private val productsSource: ProductDataSource,
    private val cartSource: ShoppingCartProductIdDataSource,
) : ShoppingProductsRepository {
    override fun loadAllProducts(page: Int): List<Product> {
        val productsData = productsSource.findByPaged(page)

        return productsData.map { productData ->
            productData.toDomain(productQuantity(productData.id))
        }
    }

    override fun loadPagedCartItem(page: Int): List<CartItemDto> {
        return cartSource.loadPagedItems(page)
    }

    override fun loadProduct(id: Long): Product = productsSource.findById(id).toDomain(productQuantity(id))

    override fun isFinalPage(page: Int): Boolean = productsSource.isFinalPage(page)

    override fun isCartFinalPage(page: Int): Boolean = cartSource.isFinalPage(page)

    override fun shoppingCartProductQuantity(): Int = cartSource.loadAll().sumOf { it.quantity }

    private fun productQuantity(productId: Long): Int {
        return try {
            cartSource.findByProductId(productId)?.quantity ?: 0
        } catch (e: NoSuchElementException) {
            0
        }
    }

    override fun increaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        cartSource.plusProductsIdCount(id, quantity)
    }

    override fun decreaseShoppingCartProduct(
        id: Long,
        quantity: Int,
    ) {
        cartSource.minusProductsIdCount(id, quantity)
    }

    override fun addShoppingCartProduct(id: Long) {
        cartSource.addedNewProductsId(ProductIdsCount(id, FIRST_QUANTITY))
    }

    override fun removeShoppingCartProduct(id: Long) {
        cartSource.removedProductsId(id)
    }

    companion object {
        private const val FIRST_QUANTITY = 1
        private const val TAG = "DefaultShoppingProductR"
    }
}

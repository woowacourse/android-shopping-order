package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.local.cart.ShoppingCartDao

class LocalShoppingCartProductIdDataSource(private val dao: ShoppingCartDao) : ShoppingCartProductIdDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? = dao.findById(productId)

    override fun loadPaged(page: Int): List<ProductIdsCountData> {
        val offset = (page - 1) * 5
        return dao.findPaged(offset)
    }

    override fun loadAll(): List<ProductIdsCountData> = dao.findAll()

    override fun isFinalPage(page: Int): Boolean {
        val count = dao.countAll()
        return page * 5 >= count
    }

    override fun addedNewProductsId(productIdsCount: ProductIdsCount): Long =
        dao.insert(
            ProductIdsCountData(productIdsCount.productId, productIdsCount.quantity),
        )

    override fun removedProductsId(productId: Long): Long {
        val product = dao.findById(productId) ?: throw NoSuchElementException()
        dao.delete(productId)
        return product.productId
    }

    override fun plusProductsIdCount(productId: Long) {
        dao.increaseQuantity(productId)
    }

    override fun minusProductsIdCount(productId: Long) {
        val oldProduct = dao.findById(productId) ?: throw NoSuchElementException()
        val newProduct = oldProduct.copy(quantity = oldProduct.quantity - 1)

        dao.update(newProduct)
    }

    override fun clearAll() {
        dao.countAll()
    }

    companion object {
        private const val TAG = "LocalShoppingCartProductIdDataSource"
    }
}

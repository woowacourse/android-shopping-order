package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product

class DefaultShoppingProductRepository(
    private val productsSource: ProductDataSource,
    private val cartSource: ShoppingCartDataSource,
) : ShoppingProductsRepository {
    private fun productQuantity(productId: Long): Int {
        return cartSource.findByProductId(productId)?.quantity ?: 0
    }

    override fun pagedProducts(page: Int): List<Product> {
        val productsData = productsSource.findByPaged(page)
        return productsData.map { productData ->
            productData.toDomain(productQuantity(productData.id))
        }
    }

    override fun allProductsUntilPage(page: Int): List<Product> {
        val productsData = productsSource.findAllUntilPage(page)
        return productsData.map { productData ->
            productData.toDomain(productQuantity(productData.id))
        }
    }

    override fun loadProduct(id: Long): Product = productsSource.findById(id).toDomain(productQuantity(id))

    override fun isFinalPage(page: Int): Boolean = productsSource.isFinalPage(page)

    override suspend fun pagedProducts2(page: Int): Result<List<Product>> =
        productsSource.findByPaged2(page).map { productsData ->
            productsData.map { productData ->
                productData.toDomain(productQuantity2(productData.id))
            }
        }

    private suspend fun productQuantity2(productId: Long): Int = cartSource.findByProductId2(productId).getOrNull()?.quantity ?: 0

    override suspend fun allProductsUntilPage2(page: Int): Result<List<Product>> =
        productsSource.findAllUntilPage2(page).map { productsData ->
            productsData.map { productData ->
                productData.toDomain(productQuantity2(productData.id))
            }
        }

    override suspend fun loadProduct2(id: Long): Result<Product> =
        productsSource.findById2(id).map { productData ->
            productData.toDomain(productQuantity2(productData.id))
        }

    override suspend fun isFinalPage2(page: Int): Result<Boolean> = productsSource.isFinalPage2(page)

    companion object {
        private const val TAG = "DefaultShoppingProductRepository"
    }
}

package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product

class DefaultShoppingProductRepository(
    private val productsSource: ProductDataSource,
    private val cartSource: ShoppingCartDataSource,
) : ShoppingProductsRepository {
    override suspend fun pagedProducts(page: Int): Result<List<Product>> =
        productsSource.findByPaged(page).map { productsData ->
            productsData.map { productData ->
                productData.toDomain(productQuantity2(productData.id))
            }
        }

    private suspend fun productQuantity2(productId: Long): Int = cartSource.findByProductId2(productId).getOrNull()?.quantity ?: 0

    override suspend fun allProductsUntilPage(page: Int): Result<List<Product>> =
        productsSource.findAllUntilPage(page).map { productsData ->
            productsData.map { productData ->
                productData.toDomain(productQuantity2(productData.id))
            }
        }

    override suspend fun loadProduct(id: Long): Result<Product> =
        productsSource.findById(id).map { productData ->
            productData.toDomain(productQuantity2(productData.id))
        }

    override suspend fun isFinalPage(page: Int): Result<Boolean> = productsSource.isFinalPage(page)

    companion object {
        private const val TAG = "DefaultShoppingProductRepository"
    }
}

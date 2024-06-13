package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(private val productRemoteDataSource: ProductRemoteDataSource) :
    ProductRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>> =
        productRemoteDataSource.getProductsByOffset(page, size).mapCatching { dto ->
            dto.toProduct()
        }

    override suspend fun find(id: Long): Result<Product> =
        productRemoteDataSource.getProductsById(id).mapCatching { dto ->
            Product(
                id = dto.id,
                imageUrl = dto.imageUrl,
                name = dto.name,
                price = dto.price,
                category = dto.category,
            )
        }

    override suspend fun getProductsByCategory(category: String): Result<List<Product>> =
        runCatching {
            var page = 0
            val products = mutableListOf<Product>()
            var loadedProducts: List<Product>
            while (page < MAX_PAGE) {
                loadedProducts =
                    productsWithCategory(category, page).getOrThrow()
                        .toMutableList()
                if (loadedProducts.isEmpty()) break
                products.addAll(loadedProducts)
                page++
            }
            products
        }

    private suspend fun productsWithCategory(
        category: String,
        page: Int,
    ): Result<List<Product>> =
        productRemoteDataSource.getProductsByCategory(category, page).mapCatching { dto ->
            dto.toProduct()
        }

    private fun ResponseProductsGetDto.toProduct() =
        this.content.map { product ->
            Product(
                id = product.id,
                imageUrl = product.imageUrl,
                name = product.name,
                price = product.price,
                category = product.category,
            )
        }

    companion object {
        private const val MAX_PAGE = 100
    }
}

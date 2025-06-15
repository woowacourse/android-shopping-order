package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.presentation.product.catalog.toUiModel

class ProductsRepositoryImpl(
    private val productsRemoteDataSource: ProductsRemoteDataSource,
    private val cartItemsRemoteDataSource: CartItemsRemoteDataSource,
) : ProductsRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): Result<PagingData> {
        return productsRemoteDataSource.getProducts(page, size)
            .mapCatching { response ->
                val products = response.content.map { it.toDomain() }
                PagingData(
                    products = products.map { it.toUiModel() },
                    hasNext = !response.last,
                    hasPrevious = !response.first,
                )
            }
    }

    override suspend fun getProductById(id: Long): Result<Product> {
        return productsRemoteDataSource.getProductById(id)
            .mapCatching { response -> response.toDomain() }
    }

    override suspend fun getRecommendProducts(category: String): Result<List<Product>> =
        runCatching {
            val categoryResult =
                productsRemoteDataSource.getProductsByCategory(category = category)
                    .getOrThrow()

            val productsByCategory = categoryResult.content.map { it.toDomain() }

            val cartResult =
                cartItemsRemoteDataSource.getCartItems(null, null)
                    .getOrThrow()

            val cartIds = cartResult.content.map { it.product.id }

            val recommendProducts =
                productsByCategory
                    .filterNot { it.id in cartIds }
                    .take(10)

            recommendProducts
        }

    private fun ProductResponse.toDomain(): Product {
        return Product(
            id = this.id,
            name = this.name,
            price = this.price,
            imageUrl = this.imageUrl,
            category = this.category,
        )
    }
}

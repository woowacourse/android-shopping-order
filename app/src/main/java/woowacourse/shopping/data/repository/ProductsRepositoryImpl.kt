package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ViewedItem
import woowacourse.shopping.data.source.local.recent.ViewedItemDataSource
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUiModel

class ProductsRepositoryImpl(
    private val productsRemoteDataSource: ProductsRemoteDataSource,
    private val viewedItemLocalDataSource: ViewedItemDataSource,
) : ProductsRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): Result<PagingData> =
        productsRemoteDataSource
            .getProducts(page, size)
            .mapCatching { response ->
                PagingData(
                    products = response.content.map { it.toDomain().toUiModel() },
                    page = response.pageable.pageNumber,
                    hasNext = !response.last,
                    hasPrevious = !response.first,
                )
            }

    override suspend fun getProductById(id: Long): Result<Product> =
        productsRemoteDataSource
            .getProductById(id)
            .mapCatching { response ->
                response.toDomain()
            }

    override suspend fun getRecommendedProductsFromLastViewed(cartProductIds: List<Long>): Result<List<Product>> {
        val lastViewedItem: ViewedItem? = viewedItemLocalDataSource.getLastViewedItem()

        return if (lastViewedItem != null) {
            getRecommendedProducts(
                category = lastViewedItem.category,
                cartProductIds = cartProductIds,
            )
        } else {
            Result.success(emptyList())
        }
    }

    private suspend fun getRecommendedProducts(
        category: String,
        cartProductIds: List<Long>,
    ): Result<List<Product>> =
        productsRemoteDataSource
            .getProductsByCategory(category)
            .mapCatching { response ->
                response.content
                    .map { it.toDomain() }
                    .filter { it.id !in cartProductIds }
                    .take(10)
            }
}

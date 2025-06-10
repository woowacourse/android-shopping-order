package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.mapper.toUiModel
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class ProductsRepositoryImpl(
    private val productsRemoteDataSource: ProductsRemoteDataSource,
    private val viewedItemRepository: ViewedItemRepository,
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

    override suspend fun getProductById(id: Long): Result<ProductUiModel> =
        productsRemoteDataSource
            .getProductById(id)
            .mapCatching { response ->
                response.toDomain().toUiModel()
            }

    override suspend fun getRecommendedProductsFromLastViewed(cartProductIds: List<Long>): Result<List<ProductUiModel>> {
        val lastViewedItem = viewedItemRepository.getLastViewedItem()

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
    ): Result<List<ProductUiModel>> =
        productsRemoteDataSource
            .getProductsByCategory(category)
            .mapCatching { response ->
                response.content
                    .map { it.toDomain() }
                    .filter { it.id !in cartProductIds }
                    .take(10)
                    .map { it.toUiModel() }
            }

    private fun ProductResponse.toDomain(): Product =
        Product(
            id = this.id,
            name = this.name,
            price = this.price,
            imageUrl = this.imageUrl,
            quantity = 0,
            category = this.category,
        )
}

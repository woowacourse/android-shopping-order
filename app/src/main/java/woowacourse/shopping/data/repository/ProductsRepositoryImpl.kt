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
    override fun getProducts(
        page: Int,
        size: Int,
        onResult: (Result<PagingData>) -> Unit,
    ) {
        productsRemoteDataSource.getProducts(page, size) { result ->
            result
                .mapCatching { response ->
                    PagingData(
                        products = response.content.map { it.toDomain().toUiModel() },
                        page = response.pageable.pageNumber,
                        hasNext = !response.last,
                        hasPrevious = !response.first,
                    )
                }
                .let(onResult)
        }
    }

    override fun getProductById(
        id: Long,
        onResult: (Result<ProductUiModel>) -> Unit,
    ) {
        productsRemoteDataSource.getProductById(id) { result ->
            result
                .mapCatching { response ->
                    response.toDomain().toUiModel()
                }
                .let(onResult)
        }
    }

    override fun getRecommendedProductsFromLastViewed(
        cartProductIds: List<Long>,
        onResult: (Result<List<ProductUiModel>>) -> Unit,
    ) {
        viewedItemRepository.getLastViewedItem { item ->
            if (item != null) {
                getRecommendedProducts(
                    category = item.category,
                    cartProductIds = cartProductIds,
                    onResult = onResult
                )
            } else {
                onResult(Result.success(emptyList()))
            }
        }
    }

    private fun getRecommendedProducts(
        category: String,
        cartProductIds: List<Long>,
        onResult: (Result<List<ProductUiModel>>) -> Unit,
    ) {
        productsRemoteDataSource.getProductsByCategory(category) { result ->
            result
                .mapCatching { response ->
                    response.content
                        .map { it.toDomain() }
                        .filter { it.id !in cartProductIds }
                        .take(10)
                        .map { it.toUiModel() }
                }
                .let(onResult)
        }
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

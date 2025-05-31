package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.mapper.toUiModel
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class ProductsRepositoryImpl(
    private val productsRemoteDataSource: ProductsRemoteDataSource,
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

    override fun getProductsByCategory(
        category: String,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        productsRemoteDataSource.getProductsByCategory(category = category) { result ->
            result
                .mapCatching { response ->
                    response.content.map { it.toDomain() }
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

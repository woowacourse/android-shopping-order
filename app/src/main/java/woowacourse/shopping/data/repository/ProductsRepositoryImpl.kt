package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository

class ProductsRepositoryImpl(
    private val productsRemoteDataSource: ProductsRemoteDataSource
) : ProductsRepository {
    override fun getProducts(onResult: (Result<List<Product>>) -> Unit) {
        productsRemoteDataSource.getProducts { result ->
            result
                .mapCatching { response ->
                    response.content.map { it.toDomain() }
                }
                .let(onResult)
        }
    }

    override fun getProductById(
        id: Int,
        onResult: (Result<Product>) -> Unit,
    ) {
        productsRemoteDataSource.getProductById(id) { result ->
            result
                .mapCatching { response ->
                    response.toDomain()
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
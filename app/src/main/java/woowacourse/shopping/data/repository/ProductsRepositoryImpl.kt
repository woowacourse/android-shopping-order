package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.data.source.remote.products.ProductsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.mapper.toUiModel
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class ProductsRepositoryImpl(
    private val productsRemoteDataSource: ProductsRemoteDataSource,
    private val cartItemsRemoteDataSource: CartItemsRemoteDataSource,
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

    override fun getRecommendProducts(
        category: String,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        productsRemoteDataSource.getProductsByCategory(category = category) { categoryResult ->
            categoryResult
                .onSuccess { productsResponse ->
                    val productsByCategory = productsResponse.content.map { it.toDomain() }
                    cartItemsRemoteDataSource.getCartItems(null, null) { cartResult ->
                        cartResult
                            .onSuccess { cartItemResponse ->
                                val cartProducts = cartItemResponse.content.map { it.product.id }
                                val recommendProducts =
                                    productsByCategory
                                        .filterNot { it.id in cartProducts }
                                        .take(10)

                                onResult(Result.success(recommendProducts))
                            }
                    }
                }
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

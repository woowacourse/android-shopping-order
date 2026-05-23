package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.remote.mapper.toDomain
import woowacourse.shopping.model.Product

class ProductRepositoryImpl(
    private val api: ProductApi,
) : ProductRepository {
    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): ProductResponseResult {
        val apiResult =
            if (category.isEmpty()) {
                api
                    .getProducts(
                        page = page,
                        size = size,
                    )
            } else {
                api.getProductsByCategory(
                    category = category,
                    page = page,
                    size = size,
                )
            }

        val products = apiResult.content.map { it.toDomain() }
        val lastPage = apiResult.last

        return ProductResponseResult(products, lastPage)
    }

    override suspend fun getProductById(id: String): Product = api.getProductById(id.toLong()).toDomain()
}

data class ProductResponseResult(
    val products: List<Product>,
    val isLastPage: Boolean,
)

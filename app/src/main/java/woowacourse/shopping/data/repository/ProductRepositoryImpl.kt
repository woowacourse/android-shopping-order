package woowacourse.shopping.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.remote.dto.response.products.ProductResponse
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

class ProductRepositoryImpl(
    private val productApi: ProductApi,
) : ProductRepository {
    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): ProductResponseResult {
        val apiResult =
            if (category.isEmpty()) {
                productApi
                    .getProducts(
                        page = page,
                        size = size,
                    )
            } else {
                productApi.getProductsByCategory(
                    category = category,
                    page = page,
                    size = size,
                )
            }

        val products = apiResult.content.map { it.toDomain() }
        val lastPage = apiResult.last

        return ProductResponseResult(products, lastPage)
    }

    override suspend fun getProductById(id: Long): Product = productApi.getProductById(id).toDomain()

    override suspend fun getProductsByIds(ids: List<Long>): List<Product> =
        coroutineScope {
            ids
                .map { id ->
                    async {
                        runCatching { getProductById(id) }.getOrNull()
                    }
                }.awaitAll()
                .filterNotNull()
        }

    private fun ProductResponse.toDomain(): Product =
        Product(
            id = id,
            name = ProductName(name),
            price = Money(price.toLong()),
            imageUrl = imageUrl,
            category = category,
        )
}

data class ProductResponseResult(
    val products: List<Product>,
    val isLastPage: Boolean,
)

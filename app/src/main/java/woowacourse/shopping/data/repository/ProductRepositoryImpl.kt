package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.remote.dto.response.products.ProductResponse
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName
import kotlin.coroutines.cancellation.CancellationException

class ProductRepositoryImpl(
    private val productApi: ProductApi,
) : ProductRepository {
    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<ProductResponseResult> =
        try {
            val response =
                if (category.isEmpty()) {
                    productApi.getProducts(
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
            Result.success(
                ProductResponseResult(
                    products = response.content.map { it.toDomain() },
                    isLastPage = response.last,
                ),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getProductById(id: Long): Result<Product> =
        try {
            Result.success(productApi.getProductById(id).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
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

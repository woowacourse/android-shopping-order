package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.remote.dto.response.product.ProductResponse
import woowacourse.shopping.data.remote.dto.response.products.ProductDto
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

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

    private fun ProductDto.toDomain(): Product =
        Product(
            id = id.toString(),
            name = ProductName(name),
            price = Money(price.toLong()),
            imageUrl = imageUrl,
            category = category,
        )

    private fun ProductResponse.toDomain(): Product =
        Product(
            id = id.toString(),
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

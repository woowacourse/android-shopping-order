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
        page: Int,
        size: Int,
    ): ProductResponseResult {
        val apiResult =
            api
                .getProducts(
                    category = "도서",
                    page = page,
                    size = size,
                )

        val products = apiResult.content.map { it.toDomain() }
        val lastPage = apiResult.last

        return ProductResponseResult(products, lastPage)
    }

    override suspend fun getProductById(id: String): Product = api.getProductById(id.toLong()).toDomain()

    private fun ProductDto.toDomain(): Product =
        Product(
            id = id.toString(),
            name = ProductName(name),
            price = Money(price),
            imageUrl = imageUrl,
        )

    private fun ProductResponse.toDomain(): Product =
        Product(
            id = id.toString(),
            name = ProductName(name),
            price = Money(price),
            imageUrl = imageUrl,
        )
}

data class ProductResponseResult(
    val products: List<Product>,
    val isLastPage: Boolean,
)

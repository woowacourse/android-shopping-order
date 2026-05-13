package woowacourse.shopping.data.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.data.remote.api.ProductApi
import woowacourse.shopping.data.remote.dto.request.Pageable
import woowacourse.shopping.data.remote.dto.response.product.ProductResponse
import woowacourse.shopping.data.remote.dto.response.products.Content
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

class ProductRepositoryImpl(
    private val api: ProductApi,
) : ProductRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int
    ): ImmutableList<Product> {
        return api.getProducts(
            category = "도서",
            pageable = Pageable(
                page = page,
                size = size,
                sort = emptyList()
            )
        ).content
            .map { it.toDomain() }
            .toImmutableList()
    }

    override suspend fun getProductById(id: String): Product {
        return api.getProductById(id.toLong()).toDomain()
    }

    private fun Content.toDomain(): Product {
        return Product(
            id = id.toString(),
            name = ProductName(name),
            price = Money(price),
            imageUrl = imageUrl
        )
    }

    private fun ProductResponse.toDomain(): Product {
        return Product(
            id = id.toString(),
            name = ProductName(name),
            price = Money(price),
            imageUrl = imageUrl
        )
    }
}
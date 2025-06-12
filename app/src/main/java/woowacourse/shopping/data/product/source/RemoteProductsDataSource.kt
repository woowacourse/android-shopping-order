package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.dto.ProductsResponse
import woowacourse.shopping.data.product.dto.ProductsResponse.Content
import woowacourse.shopping.data.product.entity.ProductEntity
import woowacourse.shopping.data.product.service.ProductService

class RemoteProductsDataSource(
    private val productService: ProductService,
) : ProductsDataSource {
    override suspend fun pagedProducts(
        page: Int,
        size: Int,
    ): ProductsResponse? {
        val response: ProductsResponse? = productService.getProducts(page, size)
        return response
    }

    override suspend fun getProductById(id: Long): ProductEntity? {
        val response: ProductResponse? = productService.getProductById(id)
        return response.toEntityOrNull()
    }

    override suspend fun getProductsByCategory(category: String): List<ProductEntity> {
        val response: ProductsResponse? =
            productService.getProductsByCategory(category)

        return response?.content?.mapNotNull {
            it.toEntityOrNull()
        } ?: emptyList()
    }

    private fun Content.toEntityOrNull(): ProductEntity? {
        return ProductEntity(
            id = id ?: return null,
            name = name ?: return null,
            price = price ?: return null,
            category = category ?: return null,
            imageUrl = imageUrl,
        )
    }

    private fun ProductResponse?.toEntityOrNull(): ProductEntity? {
        if (this == null) return null

        return ProductEntity(
            id = id ?: return null,
            name = name ?: return null,
            price = price ?: return null,
            category = category ?: return null,
            imageUrl = imageUrl,
        )
    }
}

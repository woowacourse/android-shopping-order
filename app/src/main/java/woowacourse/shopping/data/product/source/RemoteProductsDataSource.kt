package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.API
import woowacourse.shopping.data.product.PageableProductData
import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.dto.ProductsResponse
import woowacourse.shopping.data.product.dto.ProductsResponse.Content
import woowacourse.shopping.data.product.entity.ProductEntity
import woowacourse.shopping.data.product.service.ProductService

class RemoteProductsDataSource(
    private val productService: ProductService = API.productService,
) : ProductsDataSource {
    override suspend fun products(category: String): List<ProductEntity> {
        val response: ProductsResponse? =
            productService.getProducts(category = category)

        return response?.content?.mapNotNull { it.toEntityOrNull() } ?: emptyList()
    }

    override suspend fun pageableProducts(
        page: Int,
        size: Int,
    ): PageableProductData {
        val response: ProductsResponse? = productService.getProducts(page, size)

        return PageableProductData(
            products = response?.content?.mapNotNull { it.toEntityOrNull() } ?: emptyList(),
            loadable = response?.loadable == true,
        )
    }

    override suspend fun getProductById(id: Long): ProductEntity? {
        val response: ProductResponse? = productService.getProductById(id)
        return response.toEntityOrNull()
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

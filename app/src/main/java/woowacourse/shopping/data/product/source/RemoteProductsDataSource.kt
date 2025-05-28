package woowacourse.shopping.data.product.source

import woowacourse.shopping.data.ProductsHttpClient
import woowacourse.shopping.data.product.PageableProductData
import woowacourse.shopping.data.product.dto.Content
import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.entity.ProductEntity

class RemoteProductsDataSource(
    private val productsHttpClient: ProductsHttpClient = ProductsHttpClient(),
) : ProductsDataSource {
    override fun pageableProducts(
        page: Int,
        size: Int,
    ): PageableProductData {
        val response = productsHttpClient.getProducts(page, size)
        return PageableProductData(
            products = response.content?.mapNotNull { it.toEntityOrNull() } ?: emptyList(),
            loadable = response.loadable,
        )
    }

    override fun getProductById(id: Long): ProductEntity? {
        val response = productsHttpClient.getProductById(id)
        return response.toEntityOrNull()
    }

    private fun Content.toEntityOrNull(): ProductEntity? {
        return ProductEntity(
            id = id ?: return null,
            name = name ?: return null,
            price = price ?: return null,
            imageUrl = imageUrl,
        )
    }

    private fun ProductResponse.toEntityOrNull(): ProductEntity? {
        return ProductEntity(
            id = id ?: return null,
            name = name ?: return null,
            price = price ?: return null,
            imageUrl = imageUrl,
        )
    }
}

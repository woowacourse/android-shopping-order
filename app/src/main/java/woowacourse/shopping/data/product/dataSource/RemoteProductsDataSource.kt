package woowacourse.shopping.data.product.dataSource

import kotlinx.serialization.json.Json
import woowacourse.shopping.data.ProductsHttpClient
import woowacourse.shopping.data.product.PageableProducts
import woowacourse.shopping.data.product.dto.Content
import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.dto.ProductsResponse
import woowacourse.shopping.data.product.entity.ProductEntity

class RemoteProductsDataSource(
    private val productsHttpClient: ProductsHttpClient = ProductsHttpClient(),
) : ProductsDataSource {
    override fun load(
        page: Int,
        size: Int,
    ): PageableProducts {
        val jsonString: String = productsHttpClient.getProducts(page, size).body?.string() ?: ""
        val response: ProductsResponse = Json.decodeFromString<ProductsResponse>(jsonString)
        val products = response.content?.mapNotNull { it.toEntityOrNull() } ?: emptyList()
        return PageableProducts(products, response.loadable)
    }

    override fun getById(id: Long): ProductEntity? {
        val productResponse = productsHttpClient.getProductById(id)
        return productResponse.toEntityOrNull()
    }
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

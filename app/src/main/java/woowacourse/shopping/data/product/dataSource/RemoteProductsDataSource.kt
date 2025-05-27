package woowacourse.shopping.data.product.dataSource

import kotlinx.serialization.json.Json
import okhttp3.Response
import woowacourse.shopping.data.ProductsHttpClient
import woowacourse.shopping.data.product.dto.Content
import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.entity.ProductEntity

class RemoteProductsDataSource(
    private val productsHttpClient: ProductsHttpClient = ProductsHttpClient()
) : ProductsDataSource {
    override fun load(
        page: Int,
        size: Int
    ): List<ProductEntity> {
        val response: Response = productsHttpClient.getProducts(page, size)
        val jsonString: String = response.body?.string() ?: return emptyList()
        val productResponse: ProductResponse = Json.decodeFromString<ProductResponse>(jsonString)
        return productResponse.content?.mapNotNull { it.toEntityOrNull() } ?: emptyList()
    }

    override fun getById(id: Long): ProductEntity? {
        TODO("Not yet implemented")
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
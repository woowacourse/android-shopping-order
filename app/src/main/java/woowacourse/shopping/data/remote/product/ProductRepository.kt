package woowacourse.shopping.data.remote.product

import woowacourse.shopping.data.remote.NetworkClient
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.util.toDomain

class ProductRepository {
    suspend fun fetchAllProducts(): ProductResponse = NetworkClient.getProductService().requestGoods(size = Int.MAX_VALUE)

    suspend fun fetchProducts(page: Int): ProductResponse = NetworkClient.getProductService().requestGoods(page = page)

    suspend fun fetchRecommendProducts(
        latestProductId: Long,
        cartProductIds: List<Long>,
    ): List<CartProduct> {
        return try {
            val response = fetchAllProducts()
            val matchedProduct = response.content.find { it.id == latestProductId }
            val category = matchedProduct?.category ?: return emptyList()

            val recommendProducts =
                response.content
                    .filter {
                        it.category == category &&
                            it.id != latestProductId &&
                            it.id !in cartProductIds
                    }.take(10)

            recommendProducts.map {
                CartProduct(id = 0, product = it.toDomain(), quantity = 0)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun requestProductDetails(productId: Long) = NetworkClient.getProductService().requestProductDetails(productId = productId)
}

package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.local.ProductHistoryDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ProductHistoryRepositoryImpl(
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val shoppingCartRepository: ShoppingCartRepository,
) :
    ProductHistoryRepository {
    override fun insertProductHistory(
        productId: Long,
        name: String,
        price: Int,
        category: String,
        imageUrl: String,
    ): Result<Unit> =
        productHistoryDataSource.insertProductHistory(
            productId = productId,
            name = name,
            price = price,
            category = category,
            imageUrl = imageUrl,
        )

    override fun findProductHistory(productId: Long): Result<Product> =
        productHistoryDataSource.findProductHistory(productId = productId)
            .mapCatching { it.toDomain() }

    override fun getProductHistoriesByCategory(size: Int): Result<List<Cart>> {
        val recentHistory =
            productHistoryDataSource.getProductHistory(1).getOrThrow()

        if (recentHistory.isEmpty()) return Result.success(emptyList())

        return productHistoryDataSource.getProductHistoriesByCategory(category = recentHistory.first().category)
            .mapCatching { result ->
                val carts = shoppingCartRepository.getAllCarts().getOrNull()

                val productsId = carts?.content?.map { it.product.id } ?: emptyList()
                result.filter {
                    it.productId !in productsId
                }.map { Cart(product = it.toDomain()) }.take(size)
            }
    }

    override fun getProductHistory(size: Int): Result<List<Product>> =
        productHistoryDataSource.getProductHistory(size = size)
            .mapCatching { result -> result.map { it.toDomain() } }

    override fun deleteProductHistory(productId: Long): Result<Unit> = productHistoryDataSource.deleteProductHistory(productId = productId)

    override fun deleteAllProductHistory(): Result<Unit> = productHistoryDataSource.deleteAllProductHistory()
}

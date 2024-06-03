package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.local.ProductHistoryLocalDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class ProductHistoryRepositoryImpl(
    private val productHistoryLocalDataSource: ProductHistoryLocalDataSource,
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
        productHistoryLocalDataSource.insertProductHistory(
            productId = productId,
            name = name,
            price = price,
            category = category,
            imageUrl = imageUrl,
        )

    override fun getProductHistoryById(productId: Long): Result<Product> =
        productHistoryLocalDataSource.getProductHistoryById(productId = productId)

    override fun getProductHistoriesByCategory(size: Int): Result<List<Cart>> {
        val recentHistory = productHistoryLocalDataSource.getProductHistoriesBySize(1).getOrThrow()

        if (recentHistory.isEmpty()) return Result.success(emptyList())

        return productHistoryLocalDataSource.getProductHistoriesByCategory(category = recentHistory.first().category)
            .mapCatching { result ->
                val carts = shoppingCartRepository.getAllCarts().getOrNull()

                val productsId = carts?.content?.map { it.product.id } ?: emptyList()
                result.filter {
                    it.id !in productsId
                }.map { Cart(product = it) }.take(size)
            }
    }

    override fun getProductHistoriesBySize(size: Int): Result<List<Product>> =
        productHistoryLocalDataSource.getProductHistoriesBySize(size = size)

    override fun deleteProductHistoryById(productId: Long): Result<Unit> =
        productHistoryLocalDataSource.deleteProductHistoryById(productId = productId)

    override fun deleteAllProductHistories(): Result<Unit> = productHistoryLocalDataSource.deleteAllProductHistories()
}

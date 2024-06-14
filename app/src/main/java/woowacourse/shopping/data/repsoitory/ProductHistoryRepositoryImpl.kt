package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.local.ProductHistoryLocalDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingCartRemoteDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductHistoryRepository

class ProductHistoryRepositoryImpl(
    private val productHistoryLocalDataSource: ProductHistoryLocalDataSource,
    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource,
) :
    ProductHistoryRepository {
    override suspend fun insertProductHistory(
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

    override suspend fun getProductHistoryById(productId: Long): Result<Product> =
        productHistoryLocalDataSource.getProductHistoryById(productId = productId)

    override suspend fun getProductHistoriesByCategory(size: Int): Result<List<Cart>> {
        val recentHistory = productHistoryLocalDataSource.getProductHistoriesBySize(1).getOrThrow()

        if (recentHistory.isEmpty()) return Result.success(emptyList())

        return productHistoryLocalDataSource.getProductHistoriesByCategory(category = recentHistory.first().category)
            .mapCatching { result ->
                val cartTotalElement = shoppingCartRemoteDataSource.getCartsTotalElement()
                val carts = shoppingCartRemoteDataSource.getEntireCarts(cartTotalElement).content
                val productsId = carts.map { it.product.id }
                result.filter {
                    it.id !in productsId
                }.map { Cart(product = it) }.take(size)
            }
    }

    override suspend fun getProductHistoriesBySize(size: Int): Result<List<Product>> =
        productHistoryLocalDataSource.getProductHistoriesBySize(size = size)

    override suspend fun deleteProductHistoryById(productId: Long): Result<Unit> =
        productHistoryLocalDataSource.deleteProductHistoryById(productId = productId)

    override suspend fun deleteAllProductHistories(): Result<Unit> = productHistoryLocalDataSource.deleteAllProductHistories()
}

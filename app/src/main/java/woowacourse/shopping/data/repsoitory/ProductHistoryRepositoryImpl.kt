package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.local.ProductHistoryDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingCartDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductHistoryRepository

class ProductHistoryRepositoryImpl(
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val shoppingCartDataSource: ShoppingCartDataSource,
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
            productHistoryDataSource.getProductHistory(1).getOrNull() ?: return Result.success(
                emptyList(),
            )

        return productHistoryDataSource.getProductHistoriesByCategory(category = recentHistory.first().category)
            .mapCatching { result ->

                val totalElements =
                    shoppingCartDataSource.getCartProductsPaged(
                        page = ProductRepositoryImpl.FIRST_PAGE,
                        size = ProductRepositoryImpl.FIRST_SIZE,
                    ).getOrThrow().totalElements

                val carts =
                    shoppingCartDataSource.getCartProductsPaged(
                        page = ProductRepositoryImpl.FIRST_PAGE,
                        size = totalElements,
                    ).getOrThrow().toDomain()

                val productsId = carts.content.map { it.product.id }
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

    companion object {
        private var instance: ProductHistoryRepositoryImpl? = null

        fun setInstance(
            productHistoryDataSource: ProductHistoryDataSource,
            shoppingCartDataSource: ShoppingCartDataSource,
        ) {
            instance =
                ProductHistoryRepositoryImpl(
                    productHistoryDataSource = productHistoryDataSource,
                    shoppingCartDataSource = shoppingCartDataSource,
                )
        }

        fun getInstance(): ProductHistoryRepositoryImpl = requireNotNull(instance)
    }
}

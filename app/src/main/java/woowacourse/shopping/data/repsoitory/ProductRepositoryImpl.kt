package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.ShoppingCartRemoteDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val shoppingCartRemoteDataSource: ShoppingCartRemoteDataSource,
) : ProductRepository {
    override suspend fun getCartById(productId: Long): Result<Cart> {
        val cartTotalElement = shoppingCartRemoteDataSource.getCartsTotalElement()
        val carts = shoppingCartRemoteDataSource.getEntireCarts(cartTotalElement).content
        val product = productRemoteDataSource.findProductById(productId)

        return runCatching {
            carts.firstOrNull { product.id == it.product.id } ?: Cart(
                product = product,
                quantity = INIT_QUANTITY,
            )
        }
    }

    override suspend fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<Products> =
        runCatching {
            productRemoteDataSource.getPagingProduct(page, pageSize)
        }

    companion object {
        const val INIT_QUANTITY = 1
    }
}

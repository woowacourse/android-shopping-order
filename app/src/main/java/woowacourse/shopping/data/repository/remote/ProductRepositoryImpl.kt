package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDataSource: ProductDataSource,
    private val cartRepository: CartRepository,
) : ProductRepository {
    override fun fetchPagingProducts(
        page: Int,
        pageSize: Int,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        productDataSource.fetchPagingProducts(page, pageSize) { products ->
            val cartItems =
                products.map { product ->
                    cartRepository
                        .getCartItemById(product.productId) ?: CartItem(
                        product = product,
                        quantity = 0,
                    )
                }
            onResult(Result.success(cartItems))
        }
    }

    override fun fetchProductById(
        productId: Long,
        onResult: (Result<Product>) -> Unit,
    ) {
        productDataSource.fetchProductById(productId) { product ->
            onResult(Result.success(product))
        }
    }

    private fun List<Product>.toCartItems(): List<CartItem> =
        this.map { product ->
//            val quantity = cartDataSource.getQuantityById(product.productId)
            CartItem(product = product, quantity = 0)
        }
}

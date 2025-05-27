package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.CartDataSource
import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val cartDataSource: CartDataSource,
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    override fun fetchPagingProducts(
        page: Int,
        pageSize: Int,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        productDataSource.fetchPagingProducts(page, pageSize) { products ->
            val cartItems = products.toCartItems()
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
            CartItem(product, 0)
        }
}

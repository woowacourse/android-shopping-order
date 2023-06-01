package woowacourse.shopping.data.product

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ShoppingProduct
import woowacourse.shopping.domain.ShoppingProducts
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.data.server.CartRemoteDataSourceImpl
import woowacourse.shopping.data.server.ProductRemoteDataSource

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val cartRemoteDataSource: CartRemoteDataSourceImpl
) : ProductRepository {
    override fun getProducts(
        onSuccess: (ShoppingProducts) -> Unit,
        onFailure: () -> Unit
    ) {
        productRemoteDataSource.getProducts(
            onSuccess = {
                joinProductAmount(it, onSuccess, onFailure)
            },
            onFailure = { onFailure() }
        )
    }

    private fun joinProductAmount(
        products: List<Product>,
        onSuccess: (ShoppingProducts) -> Unit,
        onFailure: () -> Unit
    ) {
        cartRemoteDataSource.getCartProducts(
            onSuccess = { cartProducts ->
                val shoppingProducts = createShoppingProducts(
                    products = products,
                    cartProducts = cartProducts
                )
                onSuccess(shoppingProducts)
            },
            onFailure = { onFailure() }
        )
    }

    private fun createShoppingProducts(products: List<Product>, cartProducts: List<CartProduct>): ShoppingProducts {
        val shoppingProducts = products.map {product ->
            val cartProduct = cartProducts.find { it.product.id == product.id }
            val quantity = cartProduct?.quantity ?: 0
            ShoppingProduct(product = product, quantity = quantity)
        }
        return ShoppingProducts(shoppingProducts)
    }
}

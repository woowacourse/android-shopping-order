package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.ShoppingProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.server.ProductRemoteDataSource

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource
) : ProductRepository {
    override fun getProducts(
        startIndex: Int,
        size: Int,
        onSuccess: (Products) -> Unit,
        onFailure: () -> Unit
    ) {
        productRemoteDataSource.getProducts(
            onSuccess = {
                val endIndex = minOf(startIndex + size, it.size)
                val products = createProducts(it.subList(startIndex, endIndex))
                onSuccess(products)
            },
            onFailure = { onFailure() }
        )
    }

    private fun createProducts(products: List<Product>): Products {
        val shoppingProducts = products.map {
            ShoppingProduct(product = it, amount = 0)
        }
        return Products(shoppingProducts)
    }
}

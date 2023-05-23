package woowacourse.shopping.data.product

import woowacourse.shopping.data.database.dao.ProductDao
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.Products
import woowacourse.shopping.domain.ShoppingProduct
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.server.ProductRemoteDataSource

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val productRemoteDataSource: ProductRemoteDataSource
) : ProductRepository {
    override fun getProducts(
        startIndex: Int,
        size: Int,
        onSuccess: (Products) -> Unit,
        onFailure: () -> Unit
    ) {
        val result = productDao.selectByRange(start = startIndex, range = size)

        if (result.value.isEmpty()) {
            val path = startIndex / size + 1
            productRemoteDataSource.getProducts(
                path,
                onSuccess = {
                    val products = getProductsFromServer(it)
                    onSuccess(products)
                }
            )
        } else {
            onSuccess(result)
        }
    }

    private fun getProductsFromServer(products: List<Product>): Products {
        val shoppingProducts = products.map {
            productDao.insertProduct(it)
            ShoppingProduct(product = it, amount = 0)
        }
        return Products(shoppingProducts)
    }
}

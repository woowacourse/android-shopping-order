package woowacourse.shopping.data.product

import woowacourse.shopping.Product
import woowacourse.shopping.Products
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl constructor(
    private val productDataSource: ProductDataSource,
) : ProductRepository {
    override fun findProductById(id: Int, onSuccess: (Product?) -> Unit, onFailure: () -> Unit) {
        productDataSource.getProductById(
            id,
            onSuccess = { onSuccess(it.toDomain()) },
            onFailure = onFailure
        )
    }

    override fun getProductsWithRange(start: Int, size: Int, onSuccess: (List<Product>) -> Unit) {
        productDataSource.getAllProducts(
            onSuccess = {
                onSuccess(
                    Products(
                        it.map { productDataSource ->
                            productDataSource.toDomain()
                        }
                    ).getItemsInRange(start, size).items
                )
            },
            onFailure = {}
        )
    }
}

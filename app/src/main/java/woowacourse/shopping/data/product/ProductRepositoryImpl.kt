package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl(private val dataSource: ProductDataSource) : ProductRepository {

    override fun findAll(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit) {
        dataSource.findAll { products ->
            val slicedProducts = products.slice(offset until products.size)
                .take(limit)
            onFinish(slicedProducts)
        }
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        dataSource.findAll { products ->
            onFinish(products.size)
        }
    }

    override fun findById(id: Long, onFinish: (Product?) -> Unit) {
        dataSource.findById(id, onFinish)
    }
}

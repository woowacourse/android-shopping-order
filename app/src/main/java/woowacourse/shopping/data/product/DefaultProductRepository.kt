package woowacourse.shopping.data.product

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.repository.ProductRepository

class DefaultProductRepository(private val dataSource: ProductDataSource) : ProductRepository {

    override fun findAll(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit) {
        return dataSource.findRanged(limit, offset, onFinish)
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        return dataSource.countAll(onFinish)
    }

    override fun findById(id: Long, onFinish: (Product?) -> Unit) {
        return dataSource.findById(id, onFinish)
    }
}

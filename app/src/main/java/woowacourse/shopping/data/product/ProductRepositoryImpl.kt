package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl(private val dataSource: ProductDataSource) : ProductRepository {

    override fun findAll(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit) {
        return dataSource.findAll(limit, offset, onFinish)
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        return dataSource.countAll(onFinish)
    }

    override fun findById(id: Long, onFinish: (Product?) -> Unit) {
        return dataSource.findById(id, onFinish)
    }
}

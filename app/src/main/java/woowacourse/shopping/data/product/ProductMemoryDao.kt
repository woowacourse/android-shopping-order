package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product
import woowacourse.shopping.utils.MockData

object ProductMemoryDao : ProductDataSource {
    private val products: Map<Long, Product> = MockData.getProductList().associateBy { it.id }
    override fun findAll(onFinish: (List<Product>) -> Unit) {
        val products = products.values.toList()
        onFinish(products)
    }

    override fun findAll(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit) {
        val products = products.values.toList().slice(offset until products.values.size).take(limit)
        onFinish(products)
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        onFinish(products.values.size)
    }

    override fun findById(id: Long, onFinish: (Product?) -> Unit) {
        onFinish(products[id])
    }
}

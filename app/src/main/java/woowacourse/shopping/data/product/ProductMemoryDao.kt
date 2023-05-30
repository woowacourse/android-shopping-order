package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product
import woowacourse.shopping.utils.MockData

object ProductMemoryDao : ProductDataSource {
    private val products: Map<Long, Product> = MockData.getProductList().associateBy { it.id }
    override fun findAll(onFinish: (List<Product>) -> Unit) {
        val products = products.values.toList()
        onFinish(products)
    }

    override fun findById(id: Long, onFinish: (Product?) -> Unit) {
        onFinish(products[id])
    }
}

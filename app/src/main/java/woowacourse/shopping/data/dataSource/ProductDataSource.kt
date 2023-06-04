package woowacourse.shopping.data.dataSource

import woowacourse.shopping.model.Product

interface ProductDataSource {
    fun getAll(callback: (List<Product>?) -> Unit)
    fun findById(id: Int, callback: (Product?) -> Unit)
}

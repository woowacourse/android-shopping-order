package woowacourse.shopping.repository

import woowacourse.shopping.model.Product

interface ProductRepository {
    fun getAll(callback: (List<Product>?) -> Unit)
    fun getNext(count: Int, callback: (List<Product>?) -> Unit)
    fun findById(id: Int, callback: (Product?) -> Unit)
}

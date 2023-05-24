package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product

interface ProductDataSource {

    fun findAll(onFinish: (List<Product>) -> Unit)

    fun findAll(limit: Int, offset: Int, onFinish: (List<Product>) -> Unit)

    fun countAll(onFinish: (Int) -> Unit)

    fun findById(id: Long, onFinish: (Product?) -> Unit)
}

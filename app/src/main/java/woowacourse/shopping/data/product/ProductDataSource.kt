package woowacourse.shopping.data.product

import woowacourse.shopping.domain.Product

interface ProductDataSource {

    fun findAll(onFinish: (List<Product>) -> Unit)

    fun findById(id: Long, onFinish: (Product?) -> Unit)
}

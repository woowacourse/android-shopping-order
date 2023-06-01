package woowacourse.shopping.data.product

import woowacourse.shopping.data.entity.ProductEntity

interface ProductDataSource {

    fun findAll(onFinish: (Result<List<ProductEntity>>) -> Unit)

    fun findRanged(limit: Int, offset: Int, onFinish: (Result<List<ProductEntity>>) -> Unit)

    fun countAll(onFinish: (Result<Int>) -> Unit)

    fun findById(id: Long, onFinish: (Result<ProductEntity>) -> Unit)
}

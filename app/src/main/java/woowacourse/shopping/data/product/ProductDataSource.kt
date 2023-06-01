package woowacourse.shopping.data.product

import woowacourse.shopping.data.entity.ProductEntity

interface ProductDataSource {
    fun findAll(): Result<List<ProductEntity>>

    fun findRanged(limit: Int, offset: Int): Result<List<ProductEntity>>

    fun countAll(): Result<Int>

    fun findById(id: Long): Result<ProductEntity>
}

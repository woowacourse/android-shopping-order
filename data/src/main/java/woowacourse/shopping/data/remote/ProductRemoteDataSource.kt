package woowacourse.shopping.data.remote

import woowacourse.shopping.model.Product

interface ProductRemoteDataSource {
    fun getAll(): Result<List<Product>>
    fun findById(id: Int): Result<Product>
}

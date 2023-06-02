package woowacourse.shopping.repository

import woowacourse.shopping.domain.product.Product
import java.util.concurrent.CompletableFuture

interface ProductRepository {
    fun findAll(limit: Int, offset: Int): CompletableFuture<Result<List<Product>>>

    fun countAll(): CompletableFuture<Result<Int>>

    fun findById(id: Long): CompletableFuture<Result<Product>>
}

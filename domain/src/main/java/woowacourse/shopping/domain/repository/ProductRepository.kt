package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Product
import java.util.concurrent.CompletableFuture

interface ProductRepository {
    fun getPartially(
        size: Int,
        lastId: Int,
    ): CompletableFuture<Result<List<Product>>>
}

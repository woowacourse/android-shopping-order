package woowacourse.shopping.domain.datasource

import woowacourse.shopping.domain.model.PagingProduct
import java.util.concurrent.CompletableFuture

interface ProductDataSource {
    fun getPagingProduct(
        page: Int,
        size: Int,
    ): CompletableFuture<PagingProduct>
}

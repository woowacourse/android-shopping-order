package woowacourse.shopping.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

interface RecentlyViewedProductRepository {

    fun save(
        product: Product,
        viewedTime: LocalDateTime
    ): CompletableFuture<Result<RecentlyViewedProduct>>

    fun findFirst10OrderByViewedTimeDesc(): CompletableFuture<Result<List<RecentlyViewedProduct>>>
}

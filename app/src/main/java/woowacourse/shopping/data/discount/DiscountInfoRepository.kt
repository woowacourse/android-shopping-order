package woowacourse.shopping.data.discount

import java.util.concurrent.CompletableFuture

interface DiscountInfoRepository {

    fun getDiscountInfo(price: Int): CompletableFuture<Result<DiscountInfo>>
}

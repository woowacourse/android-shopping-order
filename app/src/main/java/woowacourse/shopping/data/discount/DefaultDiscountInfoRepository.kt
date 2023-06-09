package woowacourse.shopping.data.discount

import woowacourse.shopping.data.discount.source.NetworkDiscountInfoDataSource
import java.util.concurrent.CompletableFuture

class DefaultDiscountInfoRepository(
    private val networkDiscountInfoDataSource: NetworkDiscountInfoDataSource
) : DiscountInfoRepository {
    override fun getDiscountInfo(price: Int): CompletableFuture<Result<DiscountInfo>> {
        return CompletableFuture.supplyAsync {
            networkDiscountInfoDataSource.loadDiscountInfo(price).mapCatching {
                it.toExternal()
            }
        }
    }
}

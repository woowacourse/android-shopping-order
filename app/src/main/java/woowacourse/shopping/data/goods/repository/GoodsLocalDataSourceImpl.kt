package woowacourse.shopping.data.goods.repository

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.domain.model.Goods
import kotlin.concurrent.thread

class GoodsLocalDataSourceImpl(
    private val shoppingDatabase: ShoppingDatabase,
) : GoodsLocalDataSource {
    override suspend fun fetchRecentGoodsIds(): List<String> = shoppingDatabase.recentSeenGoodsDao().getRecentGoodsIds()

    override fun loggingRecentGoods(
        goods: Goods,
        onComplete: () -> Unit,
    ) {
        thread {
            shoppingDatabase
                .recentSeenGoodsDao()
                .addRecentGoodsWithLimit(goods.id.toString())
            Handler(Looper.getMainLooper()).post {
                onComplete()
            }
        }
    }
}

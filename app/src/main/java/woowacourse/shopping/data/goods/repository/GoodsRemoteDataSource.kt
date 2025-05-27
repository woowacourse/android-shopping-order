package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse

interface GoodsRemoteDataSource {
    fun fetchGoodsSize(onComplete: (Int) -> Unit)

    fun fetchPageGoods(
        limit: Int,
        offset: Int,
        onSuccess: (GoodsResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    )

    fun fetchGoodsById(
        id: Int,
        onComplete: (Content) -> Unit,
    )
}

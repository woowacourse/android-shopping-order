package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.data.util.mapper.toDomain
import woowacourse.shopping.domain.model.Goods

class GoodsRepositoryImpl(
    private val remoteDataSource: GoodsRemoteDataSource,
    private val localDataSource: GoodsLocalDataSource,
) : GoodsRepository {
    override fun fetchGoodsSize(onComplete: (Int) -> Unit) {
        remoteDataSource.fetchGoodsSize(onComplete)
    }

    override fun fetchPageGoods(
        limit: Int,
        offset: Int,
        onComplete: (List<Goods>) -> Unit,
        onFail: (Throwable) -> Unit,
    ) {
        remoteDataSource.fetchPageGoods(
            limit,
            offset,
            { response ->
                onComplete(getGoodsByGoodsResponse(response))
            },
            onFail,
        )
    }

    private fun getGoodsByGoodsResponse(goodsResponse: GoodsResponse): List<Goods> {
        val content = goodsResponse.content
        return content.map { it.toDomain() }
    }

    override fun fetchGoodsById(
        id: Int,
        onComplete: (Goods?) -> Unit,
    ) {
        remoteDataSource.fetchGoodsById(id, { response ->
            onComplete(response.toDomain())
        })
    }

    override fun fetchRecentGoodsIds(onComplete: (List<String>) -> Unit) {
        localDataSource.fetchRecentGoodsIds(onComplete)
    }

    override fun fetchRecentGoods(onComplete: (List<Goods>) -> Unit) {
        fetchRecentGoodsIds { recentIds ->
            if (recentIds.isEmpty()) {
                onComplete(emptyList())
                return@fetchRecentGoodsIds
            }

            val goodsList = mutableListOf<Goods>()
            var completedCount = 0

            recentIds.forEach { idString ->
                val id = idString.toIntOrNull() ?: return@forEach

                fetchGoodsById(id) { goods ->
                    goods?.let { goodsList.add(it) }
                    completedCount++

                    if (completedCount == recentIds.size) {
                        val sortedGoods =
                            goodsList.sortedBy { goods ->
                                recentIds.indexOf(goods.id.toString())
                            }
                        onComplete(sortedGoods)
                    }
                }
            }
        }
    }

    override fun fetchMostRecentGoods(onComplete: (Goods?) -> Unit) {
        fetchRecentGoodsIds { recentIds ->
            if (recentIds.isEmpty()) {
                onComplete(null)
                return@fetchRecentGoodsIds
            }
            val id = recentIds[0].toInt()
            fetchGoodsById(id) { onComplete(it) }
        }
    }

    override fun loggingRecentGoods(
        goods: Goods,
        onComplete: () -> Unit,
    ) {
        localDataSource.loggingRecentGoods(goods, onComplete)
    }
}

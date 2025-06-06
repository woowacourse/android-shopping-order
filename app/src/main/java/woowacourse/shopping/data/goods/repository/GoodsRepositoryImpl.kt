package woowacourse.shopping.data.goods.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.goods.dto.GoodsResponse
import woowacourse.shopping.data.util.mapper.toDomain
import woowacourse.shopping.domain.model.Goods

class GoodsRepositoryImpl(
    private val remoteDataSource: GoodsRemoteDataSource,
    private val localDataSource: GoodsLocalDataSource,
) : GoodsRepository {
    override suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): GoodsResponse =
        remoteDataSource.fetchPageGoods(
            limit,
            offset,
        )

    override suspend fun fetchCategoryGoods(
        limit: Int,
        category: String,
    ): GoodsResponse =
        remoteDataSource.fetchGoodsByCategory(
            limit,
            category,
        )

    override suspend fun fetchGoodsByGoodsId(goodsId: Int): Goods? = remoteDataSource.fetchGoodsDetailByGoodsId(goodsId).toDomain()

    override suspend fun fetchRecentGoodsIds(): List<String> = localDataSource.fetchRecentGoodsIds()

    override suspend fun fetchRecentGoods(): List<Goods> {
        val recentIds = fetchRecentGoodsIds()
        if (recentIds.isEmpty()) {
            return emptyList()
        }
        return goodsIdsToGoods(recentIds)
    }

    private suspend fun goodsIdsToGoods(recentIds: List<String>): List<Goods> =
        coroutineScope {
            recentIds
                .mapNotNull { it.toIntOrNull() }
                .map { id ->
                    async { fetchGoodsByGoodsId(id) }
                }.awaitAll()
                .filterNotNull()
                .sortedBy { goods ->
                    recentIds.indexOf(goods.id.toString())
                }
        }

    override suspend fun fetchMostRecentGoods(): Goods? {
        val recentIds = fetchRecentGoodsIds()
        if (recentIds.isEmpty()) {
            return null
        }
        val id = recentIds[0].toInt()
        return fetchGoodsByGoodsId(id)
    }

    override suspend fun loggingRecentGoods(goods: Goods) {
        localDataSource.loggingRecentGoods(goods)
    }
}

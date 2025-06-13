package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.data.util.mapper.toDomain
import woowacourse.shopping.domain.model.Goods

class GoodsRepositoryImpl(
    private val remoteDataSource: GoodsRemoteDataSource,
    private val localDataSource: GoodsLocalDataSource,
) : GoodsRepository {

    override suspend fun fetchGoodsSize(): Int {
        return remoteDataSource.fetchGoodsSize()
    }

    override suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): List<Goods> {
        return remoteDataSource.fetchPageGoods(limit, offset).content.map { it.toDomain() }
    }

    override suspend fun fetchCategoryGoods(
        limit: Int,
        category: String,
    ): List<Goods> {
        return remoteDataSource.fetchGoodsByCategory(limit, category).content.map { it.toDomain() }
    }

    override suspend fun fetchGoodsById(id: Int): Goods? {
        return remoteDataSource.fetchGoodsById(id).toDomain()
    }

    override suspend fun fetchRecentGoodsIds(): List<String> {
        return localDataSource.fetchRecentGoodsIds()
    }

    override suspend fun fetchRecentGoods(): List<Goods> {
        val recentIds = fetchRecentGoodsIds()
        if (recentIds.isEmpty()) return emptyList()

        val goodsList = recentIds.mapNotNull { idString ->
            val id = idString.toIntOrNull() ?: return@mapNotNull null
            fetchGoodsById(id)
        }

        return goodsList.sortedBy { recentIds.indexOf(it.id.toString()) }
    }

    override suspend fun fetchMostRecentGoods(): Goods? {
        val recentIds = fetchRecentGoodsIds()
        val id = recentIds.firstOrNull()?.toIntOrNull() ?: return null
        return fetchGoodsById(id)
    }

    override suspend fun loggingRecentGoods(goods: Goods) {
        localDataSource.loggingRecentGoods(goods)
    }
}

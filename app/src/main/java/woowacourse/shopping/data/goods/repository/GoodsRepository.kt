package woowacourse.shopping.data.goods.repository

import woowacourse.shopping.domain.model.Goods

interface GoodsRepository {
    suspend fun fetchGoodsSize(): Int

    suspend fun fetchPageGoods(
        limit: Int,
        offset: Int,
    ): List<Goods>

    suspend fun fetchCategoryGoods(
        limit: Int,
        category: String,
    ): List<Goods>

    suspend fun fetchGoodsById(
        id: Int,
    ): Goods?

    suspend fun fetchRecentGoodsIds(): List<String>

    suspend fun fetchRecentGoods(): List<Goods>

    suspend fun fetchMostRecentGoods(): Goods?

    suspend fun loggingRecentGoods(goods: Goods)
}
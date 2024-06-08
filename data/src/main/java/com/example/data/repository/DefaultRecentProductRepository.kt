package com.example.data.repository

import com.example.data.datasource.local.room.dao.RecentProductDao
import com.example.data.datasource.local.room.entity.product.toProductEntity
import com.example.data.datasource.local.room.entity.recentproduct.RecentProductEntity
import com.example.data.datasource.local.room.entity.recentproduct.toRecentProduct
import com.example.data.datasource.local.room.entity.recentproduct.toRecentProducts
import com.example.domain.repository.RecentProductRepository
import java.time.LocalDateTime
import kotlin.concurrent.thread

class DefaultRecentProductRepository(
    private val recentProductDao: RecentProductDao,
) : RecentProductRepository {
    override fun findLastOrNull(): com.example.domain.model.RecentProduct? {
        var lastRecentProductEntity: RecentProductEntity? = null
        thread {
            lastRecentProductEntity = recentProductDao.findRange(1).firstOrNull()
        }.join()
        return lastRecentProductEntity?.toRecentProduct()
    }

    override fun findRecentProducts(): List<com.example.domain.model.RecentProduct> {
        var lastRecentProductEntity: List<RecentProductEntity> = emptyList()
        thread {
            lastRecentProductEntity = recentProductDao.findRange(FIND_RECENT_PRODUCTS_COUNT)
        }.join()
        return lastRecentProductEntity.toRecentProducts()
    }

    override fun save(product: com.example.domain.model.Product) {
        thread {
            if (recentProductDao.findOrNull(product.id) == null) {
                recentProductDao.insert(
                    RecentProductEntity(
                        product = product.toProductEntity(),
                        seenDateTime = LocalDateTime.now(),
                    ),
                )
                return@thread
            }
            recentProductDao.update(product.id, LocalDateTime.now())
        }.join()
    }

    override fun getRecommendProducts(cartItems: List<com.example.domain.model.CartItem>): List<com.example.domain.model.Product> {
        var recommendProducts: List<com.example.domain.model.Product> = emptyList()
        thread {
            val category = findLastOrNull()?.product?.category ?: return@thread
            val categoryProducts = recentProductDao.findCategory(category).toRecentProducts()
            val recommendCategoryProducts =
                categoryProducts.filter { recentProduct ->
                    cartItems.none { it.product.id == recentProduct.product.id }
                }
            recommendProducts =
                recommendCategoryProducts
                    .take(RECOMMEND_PRODUCTS_COUNT)
                    .map { it.product }
        }.join()
        return recommendProducts
    }

    companion object {
        private const val FIND_RECENT_PRODUCTS_COUNT = 10
        private const val RECOMMEND_PRODUCTS_COUNT = 10
    }
}

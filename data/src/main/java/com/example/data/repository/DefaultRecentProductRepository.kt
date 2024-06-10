package com.example.data.repository

import com.example.data.datasource.local.dao.RecentProductDao
import com.example.data.datasource.local.entity.product.toProductEntity
import com.example.data.datasource.local.entity.recentproduct.RecentProductEntity
import com.example.data.datasource.local.entity.recentproduct.toRecentProduct
import com.example.data.datasource.local.entity.recentproduct.toRecentProducts
import com.example.domain.model.CartItem
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import com.example.domain.repository.RecentProductRepository
import kotlin.concurrent.thread

class DefaultRecentProductRepository(
    private val recentProductDao: RecentProductDao,
) : RecentProductRepository {
    override fun findLastOrNull(): RecentProduct? {
        var lastRecentProductEntity: RecentProductEntity? = null
        thread {
            lastRecentProductEntity = recentProductDao.findRange(1).firstOrNull()
        }.join()
        return lastRecentProductEntity?.toRecentProduct()
    }

    override fun findRecentProducts(): List<RecentProduct> {
        var lastRecentProductEntity: List<RecentProductEntity> = emptyList()
        thread {
            lastRecentProductEntity = recentProductDao.findRange(FIND_RECENT_PRODUCTS_COUNT)
        }.join()
        return lastRecentProductEntity.toRecentProducts()
    }

    override fun save(product: Product) {
        thread {
            if (recentProductDao.findOrNull(product.id) == null) {
                recentProductDao.insert(
                    RecentProductEntity(
                        product = product.toProductEntity(),
                        seenDateTime = System.currentTimeMillis(),
                    ),
                )
                return@thread
            }
            recentProductDao.update(product.id, System.currentTimeMillis())
        }.join()
    }

    override fun getRecommendProducts(cartItems: List<CartItem>): List<Product> {
        var recommendProducts: List<Product> = emptyList()
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

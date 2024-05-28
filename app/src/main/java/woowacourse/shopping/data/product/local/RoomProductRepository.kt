package woowacourse.shopping.data.product.local

import woowacourse.shopping.data.product.dummyProducts
import woowacourse.shopping.data.product.local.dao.ProductDao
import woowacourse.shopping.data.product.local.entity.ProductEntity
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class RoomProductRepository(private val productDao: ProductDao) : ProductRepository {
    init {
        thread {
            productDao.insertAll(dummyProducts.toProductEntities())
        }
    }

    override fun find(id: Int): Product {
        var productEntity: ProductEntity? = null
        thread {
            productEntity = productDao.findOrNull(id)
        }.join()
        return productEntity?.toProduct() ?: throw IllegalArgumentException(INVALID_ID_MESSAGE)
    }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): List<Product> {
        var productEntities: List<ProductEntity> = emptyList()
        thread {
            productEntities = productDao.findRange(page, pageSize)
        }.join()
        return productEntities.toProducts()
    }

    override fun totalProductCount(): Int {
        var totalProductCount = 0
        thread {
            totalProductCount = productDao.totalCount()
        }.join()
        return totalProductCount
    }

    companion object {
        private const val INVALID_ID_MESSAGE = "해당하는 id의 상품이 존재하지 않습니다."
    }
}

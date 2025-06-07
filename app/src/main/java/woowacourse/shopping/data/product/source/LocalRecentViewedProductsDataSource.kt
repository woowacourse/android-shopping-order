package woowacourse.shopping.data.product.source

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.product.dao.RecentViewedProductDao
import woowacourse.shopping.data.product.database.RecentViewedProductDatabase
import woowacourse.shopping.data.product.entity.RecentViewedProductEntity

object LocalRecentViewedProductsDataSource : RecentViewedProductsDataSource {
    private lateinit var dao: RecentViewedProductDao

    fun init(applicationContext: Context) {
        val db =
            Room
                .databaseBuilder(
                    applicationContext,
                    RecentViewedProductDatabase::class.java,
                    "recentViewedProducts",
                ).build()

        dao = db.dao()
    }

    override suspend fun load(): List<RecentViewedProductEntity> = dao.loadProducts()

    override suspend fun upsert(product: RecentViewedProductEntity) {
        val count = dao.productsSize()
        if (count < MAX_ENTITY_COUNT) {
            dao.upsertProduct(product)
            return
        }

        dao.deleteProduct(count - MAX_ENTITY_COUNT)
        dao.upsertProduct(product)
    }

    private const val MAX_ENTITY_COUNT = 10
}

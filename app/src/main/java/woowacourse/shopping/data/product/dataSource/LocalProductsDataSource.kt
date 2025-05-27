package woowacourse.shopping.data.product.dataSource

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.product.dao.ProductDao
import woowacourse.shopping.data.product.database.ProductDatabase
import woowacourse.shopping.data.product.entity.ProductEntity

object LocalProductsDataSource : ProductsDataSource {
    private lateinit var dao: ProductDao

    fun init(applicationContext: Context) {
        val db =
            Room
                .databaseBuilder(
                    applicationContext,
                    ProductDatabase::class.java,
                    "products",
                ).build()

        dao = db.dao()
    }

    override fun load(page: Int, size: Int): List<ProductEntity> = dao.load()

    override fun getById(id: Long): ProductEntity? = dao.load(id)
}

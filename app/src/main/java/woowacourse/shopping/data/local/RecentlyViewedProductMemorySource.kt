package woowacourse.shopping.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import woowacourse.shopping.data.database.ProductContract
import woowacourse.shopping.data.datasource.RecentlyViewedProductDataSource
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import java.time.LocalDateTime

class RecentlyViewedProductMemorySource(private val db: SQLiteDatabase) :
    RecentlyViewedProductDataSource {
    override fun save(product: Product, viewedTime: LocalDateTime): Result<RecentlyViewedProduct> {
        return runCatching {
            val selection =
                "${ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_PRODUCT_ID} = ?"
            val selectionArgs = arrayOf(product.id.toString())
            db.delete(
                ProductContract.RecentlyViewedProductEntry.TABLE_NAME, selection, selectionArgs
            )

            val value = ContentValues().apply {
                put(
                    ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_PRODUCT_ID, product.id
                )
                put(
                    ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_VIEWED_TIME,
                    viewedTime.toString()
                )
            }
            val id = db.insert(ProductContract.RecentlyViewedProductEntry.TABLE_NAME, null, value)
            RecentlyViewedProduct(id, product, viewedTime)
        }
    }

    override fun findLimitedOrderByViewedTimeDesc(limit: Int): Result<List<RecentlyViewedProductEntity>> {
        val recentlyViewedProducts = mutableListOf<RecentlyViewedProductEntity>()

        return runCatching {
            val cursor = db.rawQuery(
                """
            SELECT * FROM ${ProductContract.RecentlyViewedProductEntry.TABLE_NAME}
            ORDER BY ${ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_VIEWED_TIME} DESC
            LIMIT $limit
                """.trimIndent(),
                null
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val productId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_PRODUCT_ID))
                val viewedTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_VIEWED_TIME))
                recentlyViewedProducts.add(
                    RecentlyViewedProductEntity(
                        id, productId, viewedTime
                    )
                )
            }

            cursor.close()
            recentlyViewedProducts
        }
    }
}

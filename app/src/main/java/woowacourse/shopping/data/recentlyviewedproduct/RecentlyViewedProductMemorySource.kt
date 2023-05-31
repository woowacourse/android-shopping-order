package woowacourse.shopping.data.recentlyviewedproduct

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import woowacourse.shopping.data.database.ProductContract
import woowacourse.shopping.data.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.recentlyviewedproduct.RecentlyViewedProduct
import java.time.LocalDateTime

class RecentlyViewedProductMemorySource(private val db: SQLiteDatabase) :
    RecentlyViewedProductDataSource {
    override fun save(
        product: Product, viewedTime: LocalDateTime, onFinish: (RecentlyViewedProduct) -> Unit
    ) {
        deleteRecentlyViewedProductIfSameProductExists(product)

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
        onFinish(RecentlyViewedProduct(id, product, viewedTime))
    }

    private fun deleteRecentlyViewedProductIfSameProductExists(product: Product) {
        val selection = "${ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_PRODUCT_ID} = ?"
        val selectionArgs = arrayOf(product.id.toString())
        db.delete(ProductContract.RecentlyViewedProductEntry.TABLE_NAME, selection, selectionArgs)
    }

    override fun findFirst10OrderByViewedTimeDesc(onFinish: (List<RecentlyViewedProductEntity>) -> Unit) {
        val recentlyViewedProducts = mutableListOf<RecentlyViewedProductEntity>()
        val limit = 10
        val cursor = db.rawQuery(
            """
            SELECT * FROM ${ProductContract.RecentlyViewedProductEntry.TABLE_NAME}
            ORDER BY ${ProductContract.RecentlyViewedProductEntry.COLUMN_NAME_VIEWED_TIME} DESC
            LIMIT $limit
            """.trimIndent(), null
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
        onFinish(recentlyViewedProducts)
    }
}

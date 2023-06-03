package woowacourse.shopping.data.local.recentlyViewed

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.provider.BaseColumns
import woowacourse.shopping.data.dataSource.RecentlyViewedDataSource
import woowacourse.shopping.data.local.WoowaShoppingContract.RecentlyViewed.TABLE_COLUMN_IMAGE_URL
import woowacourse.shopping.data.local.WoowaShoppingContract.RecentlyViewed.TABLE_COLUMN_NAME
import woowacourse.shopping.data.local.WoowaShoppingContract.RecentlyViewed.TABLE_COLUMN_PRICE
import woowacourse.shopping.data.local.WoowaShoppingContract.RecentlyViewed.TABLE_COLUMN_PRODUCT_ID
import woowacourse.shopping.data.local.WoowaShoppingContract.RecentlyViewed.TABLE_COLUMN_SERVER
import woowacourse.shopping.data.local.WoowaShoppingContract.RecentlyViewed.TABLE_COLUMN_VIEWED_DATE_TIME
import woowacourse.shopping.data.local.WoowaShoppingContract.RecentlyViewed.TABLE_NAME
import woowacourse.shopping.data.local.WoowaShoppingDbHelper
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.domain.model.Product
import java.time.LocalDateTime

class RecentlyViewedDao(context: Context) : RecentlyViewedDataSource {
    private val shoppingDb by lazy { WoowaShoppingDbHelper(context).readableDatabase }

    override fun getRecentlyViewedProducts(unit: Int): List<RecentlyViewedEntity> {
        val server = ServicePool.server.name
        val query = "SELECT * FROM $TABLE_NAME WHERE $TABLE_COLUMN_SERVER = '$server' ORDER BY ${BaseColumns._ID} DESC LIMIT $unit"
        val cursor = shoppingDb.rawQuery(query, null)
        val itemContainer = mutableListOf<RecentlyViewedEntity>()
        while (cursor.moveToNext()) {
            itemContainer.add(readRecentlyViewed(cursor))
        }
        cursor.close()
        return itemContainer
    }

    override fun getLastViewedProduct(): RecentlyViewedEntity? {
        val server = ServicePool.server.name
        val query =
            "SELECT * FROM $TABLE_NAME WHERE $TABLE_COLUMN_SERVER = '$server' ORDER BY $TABLE_COLUMN_VIEWED_DATE_TIME DESC LIMIT 1"
        val cursor: Cursor = shoppingDb.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            return readRecentlyViewed(cursor)
        }
        cursor.close()
        return null
    }

    override fun addRecentlyViewedProduct(product: Product): Long {
        val data = ContentValues()
        data.put(TABLE_COLUMN_VIEWED_DATE_TIME, (LocalDateTime.now().toString()))
        data.put(TABLE_COLUMN_PRODUCT_ID, product.id)
        data.put(TABLE_COLUMN_IMAGE_URL, product.imageUrl)
        data.put(TABLE_COLUMN_NAME, product.name)
        data.put(TABLE_COLUMN_PRICE, product.price)
        data.put(TABLE_COLUMN_SERVER, ServicePool.server.name)
        val id =
            shoppingDb.insertWithOnConflict(TABLE_NAME, null, data, CONFLICT_REPLACE)
        return id
    }

    private fun readRecentlyViewed(cursor: Cursor): RecentlyViewedEntity {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val viewedDateTime = LocalDateTime.parse(
            cursor.getString(cursor.getColumnIndexOrThrow(TABLE_COLUMN_VIEWED_DATE_TIME)),
        )
        val productId = cursor.getLong(cursor.getColumnIndexOrThrow(TABLE_COLUMN_PRODUCT_ID))
        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(TABLE_COLUMN_IMAGE_URL))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(TABLE_COLUMN_NAME))
        val price = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_COLUMN_PRICE))
        return RecentlyViewedEntity(id, viewedDateTime, productId, imageUrl, name, price)
    }
}

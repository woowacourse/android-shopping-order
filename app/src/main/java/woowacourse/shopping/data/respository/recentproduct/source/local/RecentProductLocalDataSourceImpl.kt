package woowacourse.shopping.data.respository.recentproduct.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import woowacourse.shopping.data.database.RecentProductContract
import woowacourse.shopping.data.database.RecentProductHelper
import woowacourse.shopping.data.database.getTableName
import woowacourse.shopping.data.model.RecentProductEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSourceImpl
import woowacouse.shopping.model.recentproduct.RecentProduct
import woowacouse.shopping.model.recentproduct.RecentProducts
import java.time.LocalDateTime

class RecentProductLocalDataSourceImpl(
    context: Context,
    server: Server,
) : RecentProductLocalDataSource {
    private val db = RecentProductHelper(context).writableDatabase
    private val productDataSource = ProductRemoteDataSourceImpl(server)
    private val tableName = getTableName(server)

    override fun insertRecentProduct(productId: Long) {
        val value = ContentValues().apply {
            put(RecentProductContract.RecentProduct.PRODUCT_ID, productId)
            put(RecentProductContract.RecentProduct.CREATE_DATE, LocalDateTime.now().toString())
        }
        if (checkRecentProduct(productId)) {
            db.update(
                tableName,
                value,
                "${RecentProductContract.RecentProduct.PRODUCT_ID} = ? ",
                arrayOf(productId.toString()),
            )
            return
        }
        db.insert(tableName, null, value)
    }

    private fun checkRecentProduct(selectProductId: Long): Boolean {
        val recentProduct = selectRecentProductByProductId(selectProductId)
        if (recentProduct.isEmpty()) return false
        return true
    }

    private fun selectRecentProductByProductId(selectProductId: Long): List<RecentProductEntity> {
        val result = mutableListOf<RecentProductEntity>()
        val cursor = getCursorByProductId(selectProductId, 1)

        with(cursor) {
            while (moveToNext()) {
                val recentProductId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val productId =
                    getLong(getColumnIndexOrThrow(RecentProductContract.RecentProduct.PRODUCT_ID))
                result.add(RecentProductEntity(recentProductId, productId))
            }
        }
        cursor.close()

        return result
    }

    override fun deleteNotToday(today: String) {
        val sql =
            "DELETE FROM $tableName WHERE ${RecentProductContract.RecentProduct.CREATE_DATE} NOT LIKE '$today%'"

        db.execSQL(sql)
    }

    override fun getAllRecentProducts(limit: Int, onSuccess: (RecentProducts) -> Unit) {
        productDataSource.requestDatas({}) { products ->
            val recentProductEntities = getAllRecentProductsIds(limit)
            val recentProducts = (
                recentProductEntities.mapNotNull { recentProductEntity ->
                    products.find { it.id == recentProductEntity.productId }?.let { product ->
                        RecentProduct(recentProductEntity.id, product)
                    }
                }
                ).toList()
            onSuccess(RecentProducts(recentProducts))
        }
    }

    private fun getAllRecentProductsIds(limit: Int): List<RecentProductEntity> {
        val result = mutableListOf<RecentProductEntity>()
        val cursor = getCursor(limit)
        with(cursor) {
            while (moveToNext()) {
                val recentProductId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val productId =
                    getLong(getColumnIndexOrThrow(RecentProductContract.RecentProduct.PRODUCT_ID))
                result.add(RecentProductEntity(recentProductId, productId))
            }
        }
        cursor.close()
        if (result.isEmpty()) result.add(getErrorData())

        return result.toList()
    }

    private fun getCursor(limit: Int): Cursor {
        return db.query(
            tableName,
            null,
            null,
            null,
            null,
            null,
            RecentProductContract.RecentProduct.CREATE_DATE + " DESC",
            limit.toString(),
        )
    }

    private fun getCursorByProductId(productId: Long, limit: Int): Cursor {
        return db.query(
            tableName,
            null,
            "${RecentProductContract.RecentProduct.PRODUCT_ID} = ?",
            arrayOf(productId.toString()),
            null,
            null,
            null,
            limit.toString(),
        )
    }

    private fun getErrorData() = RecentProductEntity(
        id = -1L,
        productId = -1L,
    )
}

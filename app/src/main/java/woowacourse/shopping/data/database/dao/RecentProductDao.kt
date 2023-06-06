package woowacourse.shopping.data.database.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import woowacourse.shopping.data.database.table.SqlRecentProduct
import woowacourse.shopping.data.entity.RecentProductEntity
import java.time.LocalDateTime

class RecentProductDao(private val database: SQLiteDatabase, private val server: String) {
    fun insertRecentProduct(recentProduct: RecentProductEntity) {
        val row = ContentValues()
        row.put(SqlRecentProduct.PRODUCT_ID, recentProduct.id)
        row.put(SqlRecentProduct.SERVER_NAME, server)
        row.put(SqlRecentProduct.TIME, recentProduct.time.toString())
        database.insert(SqlRecentProduct.name, null, row)
    }

    fun selectAll(): List<RecentProductEntity> {
        val cursor = database.rawQuery(
            """
                |SELECT * FROM ${SqlRecentProduct.name} 
                |WHERE ${SqlRecentProduct.SERVER_NAME} = ?
                |ORDER BY ${SqlRecentProduct.TIME} DESC
            """.trimMargin(),
            arrayOf(server)
        )
        return createRecentProducts(cursor)
    }

    private fun createRecentProducts(cursor: Cursor): List<RecentProductEntity> {
        return cursor.use {
            val recentProducts = mutableListOf<RecentProductEntity>()
            while (it.moveToNext()) {
                recentProducts.add(createRecentProduct(it))
            }
            recentProducts
        }
    }

    fun selectProduct(id: Int): RecentProductEntity? {
        val cursor = database.rawQuery(
            """
                |SELECT * FROM ${SqlRecentProduct.name} 
                |WHERE ${SqlRecentProduct.PRODUCT_ID} = ? AND ${SqlRecentProduct.SERVER_NAME} = ?
            """.trimMargin(),
            arrayOf(id.toString(), server)
        )
        return cursor.use {
            if (it.moveToNext()) createRecentProduct(it)
            else null
        }
    }

    private fun createRecentProduct(cursor: Cursor) = RecentProductEntity(
        cursor.getInt(cursor.getColumnIndexOrThrow(SqlRecentProduct.PRODUCT_ID)),
        LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(SqlRecentProduct.TIME)))
    )

    fun updateRecentProduct(recentProduct: RecentProductEntity) {
        val row = ContentValues()
        row.put(SqlRecentProduct.PRODUCT_ID, recentProduct.id)
        row.put(SqlRecentProduct.TIME, recentProduct.time.toString())

        database.update(
            SqlRecentProduct.name,
            row,
            "${SqlRecentProduct.PRODUCT_ID} = ? AND ${SqlRecentProduct.SERVER_NAME} = ?",
            arrayOf(recentProduct.id.toString(), server)
        )
    }

    fun selectLatestRecentProduct(): RecentProductEntity? {
        val cursor = database.rawQuery(
            """
                |SELECT * FROM ${SqlRecentProduct.name}
                |WHERE ${SqlRecentProduct.SERVER_NAME} = ?
                |ORDER BY ${SqlRecentProduct.TIME} DESC
                |LIMIT 1
            """.trimMargin(),
            arrayOf(server)
        )

        return cursor.use {
            if (it.moveToNext()) createRecentProduct(cursor)
            else null
        }
    }
}

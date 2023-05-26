package woowacourse.shopping.data.respository.cart.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import woowacourse.shopping.data.database.CartContract
import woowacourse.shopping.data.database.CartHelper
import woowacourse.shopping.data.database.getTableName
import woowacourse.shopping.data.model.CartLocalEntity
import woowacourse.shopping.data.model.Server

class CartLocalDataSourceImpl(
    context: Context,
    server: Server,
) : CartLocalDataSource {
    private val db = CartHelper(context).writableDatabase
    private val tableName = getTableName(server)

    override fun insertCart(cartId: Long) {
        val value = ContentValues().apply {
            put(CartContract.Cart.ID, cartId)
            put(CartContract.Cart.CHECKED, 1)
        }
        db.insert(tableName, null, value)
    }

    override fun deleteCart(cartId: Long) {
        db.delete(
            tableName,
            "${CartContract.Cart.ID} = ?",
            arrayOf(cartId.toString()),
        )
    }

    override fun updateCartChecked(cartId: Long, isChecked: Boolean) {
        val checked = if (isChecked) 1 else 0

        val value = ContentValues().apply {
            put(CartContract.Cart.CHECKED, checked)
        }

        db.update(
            tableName,
            value,
            "${CartContract.Cart.ID} = ?",
            arrayOf(cartId.toString()),
        )
    }

    override fun selectAllCarts(): List<CartLocalEntity> {
        return getItems(getCursorAll())
    }

    private fun getItems(cursor: Cursor): List<CartLocalEntity> {
        val result = mutableListOf<CartLocalEntity>()
        with(cursor) {
            while (moveToNext()) {
                val cartId = getLong(getColumnIndexOrThrow(CartContract.Cart.ID))
                val checked = getInt(getColumnIndexOrThrow(CartContract.Cart.CHECKED))
                result.add(CartLocalEntity(cartId, checked))
            }
        }
        cursor.close()

        return result.toList()
    }

    private fun getCursorAll(): Cursor =
        db.query(
            tableName,
            null,
            null,
            null,
            null,
            null,
            null,
        )
}

package woowacourse.shopping.data.respository.cart.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import woowacourse.shopping.data.database.CartContract
import woowacourse.shopping.data.database.CartHelper
import woowacourse.shopping.data.database.getTableName
import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.model.entity.CartLocalEntity
import woowacouse.shopping.model.cart.CartProduct

class CartLocalDataSourceImpl(
    context: Context,
    url: Server.Url,
) : CartLocalDataSource {
    private val db = CartHelper(context)
    private val tableName = getTableName(url)

    override fun insertCart(cartId: Long) {
        val db = this.db.writableDatabase
        if (hasCartId(cartId)) return

        val value = ContentValues().apply {
            put(CartContract.Cart.ID, cartId)
            put(CartContract.Cart.CHECKED, 1)
        }
        db.insert(tableName, null, value)
        db.close()
    }

    private fun hasCartId(id: Long): Boolean {
        val db = this.db.readableDatabase
        val cursor = db.query(
            tableName,
            null,
            "${CartContract.Cart.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        )

        if (cursor.moveToFirst().not()) {
            cursor.close()
            return false
        }
        cursor.close()
        db.close()

        return true
    }

    override fun deleteCart(cartId: Long) {
        val db = this.db.writableDatabase
        db.delete(
            tableName,
            "${CartContract.Cart.ID} = ?",
            arrayOf(cartId.toString()),
        )
        db.close()
    }

    override fun deleteCarts(cartIds: List<Long>) {
        val db = this.db.writableDatabase
        cartIds.forEach {
            db.delete(
                tableName,
                "${CartContract.Cart.ID} = ?",
                arrayOf(it.toString())
            )
        }
        db.close()
    }

    override fun updateCartChecked(cartId: Long, isChecked: Boolean) {
        val db = this.db.writableDatabase
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
        db.close()
    }

    override fun selectAllCarts(): List<CartProduct> {
        return getItems(getCursorAll()).map { it.toModel() }
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

    private fun getCursorAll(): Cursor {
        val db = this.db.readableDatabase

        return db.query(
            tableName,
            null,
            null,
            null,
            null,
            null,
            null,
        )
    }
}

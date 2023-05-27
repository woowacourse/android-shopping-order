package woowacourse.shopping.database.cart

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import woowacourse.shopping.data.localDataSource.CartLocalDataSource
import woowacourse.shopping.database.ShoppingDBHelper
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.Product

class CartDatabase(context: Context) : CartLocalDataSource {
    private val db = ShoppingDBHelper(context).writableDatabase

    private lateinit var cartProducts: CartProducts

    override fun getAll(callback: (CartProducts) -> Unit) {
        val cartProducts = mutableListOf<CartProduct>()
        getCartCursor().use {
            while (it.moveToNext()) {
                cartProducts.add(getCartProduct(it))
            }
            callback(CartProducts(cartProducts))
        }
    }

    @SuppressLint("Range")
    private fun getCartProduct(cursor: Cursor): CartProduct {
        CartConstant.fromCursor(cursor).let {
            return CartProduct(
                id = it.id,
                quantity = it.quantity,
                product = Product(
                    id = it.product.id,
                    name = it.product.name,
                    price = it.product.price,
                    imageUrl = it.product.imageUrl
                )
            )
        }
    }

    private fun getCartCursor(): Cursor {
        return db.rawQuery(CartConstant.getGetAllQuery(), null)
    }

    override fun getPage(index: Int, size: Int, callback: (CartProducts) -> Unit) {
        callback(cartProducts.subList(index * size, size))
    }

    override fun hasNextPage(index: Int, size: Int): Boolean {
        return cartProducts.hasNextPage(index, size)
    }

    override fun hasPrevPage(index: Int, size: Int): Boolean {
        return cartProducts.hasPrevPage(index, size)
    }

    override fun getTotalCount(): Int {
        return cartProducts.size
    }

    override fun getTotalSelectedCount(): Int {
        return cartProducts.totalCheckedQuantity
    }

    override fun getTotalPrice(): Int {
        return cartProducts.totalPrice
    }

    override fun insert(productId: Int) {
        getAll { cartProducts = it }
    }

    override fun updateCount(id: Int, count: Int, callback: (Int?) -> Unit) {
        val sql = when {
            count < 1 -> CartConstant.getDeleteQuery(id)
            else -> CartConstant.getUpdateCountQuery(id, count)
        }
        db.execSQL(sql).let {
            getAll { cartProducts = it }
            callback(count)
        }
    }

    override fun remove(id: Int, callback: () -> Unit) {
        db.execSQL(CartConstant.getDeleteQuery(id))
        getAll { cartProducts = it }
        callback()
    }
}

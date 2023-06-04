package woowacourse.shopping.database.cart

import android.content.Context
import android.database.Cursor
import woowacourse.shopping.data.localDataSource.CartLocalDataSource
import woowacourse.shopping.database.ShoppingDBHelper
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.Product

class CartSqliteDataSource(context: Context) : CartLocalDataSource {
    private val db = ShoppingDBHelper(context).writableDatabase

    private lateinit var cartProducts: CartProducts

    override fun getAll(): Result<CartProducts> {
        val cartProducts = mutableListOf<CartProduct>()
        getCartCursor().use {
            while (it.moveToNext()) {
                cartProducts.add(getCartProduct(it))
            }
            this.cartProducts = CartProducts(cartProducts)
            return (Result.success(CartProducts(cartProducts)))
        }
    }

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

    override fun getPage(index: Int, size: Int): Result<CartProducts> {
        return Result.success(cartProducts.subList(index * size, size))
    }

    override fun hasNextPage(index: Int, size: Int): Boolean {
        return cartProducts.size > (index + 1) * size
    }

    override fun hasPrevPage(index: Int, size: Int): Boolean {
        return index > 0
    }

    override fun getTotalCount(): Int {
        return cartProducts.size
    }

    override fun getTotalCheckedCount(): Int {
        return cartProducts.totalCheckedQuantity
    }

    override fun getTotalPrice(): Int {
        return cartProducts.totalPrice
    }

    override fun insert(cartProduct: CartProduct) = runCatching {
        db.execSQL(CartConstant.getInsertQuery(cartProduct))
        1
    }

    override fun updateCount(id: Int, count: Int): Result<Int> = runCatching {
        db.execSQL(CartConstant.getUpdateCountQuery(id, count))
        count
    }

    override fun remove(id: Int) = runCatching {
        db.execSQL(CartConstant.getDeleteQuery(id))
        0
    }
}

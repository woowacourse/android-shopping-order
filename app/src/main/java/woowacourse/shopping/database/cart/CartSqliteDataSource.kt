package woowacourse.shopping.database.cart

import android.content.Context
import android.database.Cursor
import woowacourse.shopping.data.localDataSource.CartLocalDataSource
import woowacourse.shopping.database.ShoppingDBHelper
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.Product

class CartSqliteDataSource(context: Context) : CartLocalDataSource {
    private val db = ShoppingDBHelper(context).writableDatabase

    private lateinit var cartProducts: List<CartProduct>

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

    override fun getPage(offset: Int, size: Int): Result<List<CartProduct>> {
        CartConstant.getPagingQuery(offset, size).let {
            getCartCursor().use { cursor ->
                val cartProducts = mutableListOf<CartProduct>()
                while (cursor.moveToNext()) {
                    cartProducts.add(getCartProduct(cursor))
                }
                return Result.success(cartProducts)
            }
        }
    }

    override fun getChecked(): Result<List<CartProduct>> {
        TODO("Not yet implemented")
    }

    override fun getByProductId(productId: Int): Result<CartProduct> {
        TODO("Not yet implemented")
    }

    override fun getCurrentPage(): Int {
        TODO("Not yet implemented")
    }

    override fun getTotalCount(): Int {
        return cartProducts.size
    }

    override fun getTotalCheckedCount(): Int {
        return cartProducts.count { it.checked }
    }

    override fun getCurrentPageChecked(): Int {
        TODO("Not yet implemented")
    }

    override fun setCurrentPageChecked(checked: Boolean) {
        TODO("Not yet implemented")
    }

    override fun hasNextPage(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasPrevPage(): Boolean {
        TODO("Not yet implemented")
    }

    override fun replaceAll(cartProducts: List<CartProduct>) {
        TODO("Not yet implemented")
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getTotalPrice(): Int {
        return cartProducts.sumOf { it.product.price * it.quantity }
    }
}

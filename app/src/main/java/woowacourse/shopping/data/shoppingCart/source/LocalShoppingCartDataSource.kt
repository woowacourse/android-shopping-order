package woowacourse.shopping.data.shoppingCart.source

import android.content.Context
import androidx.room.Room
import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.data.shoppingCart.PageableCartItemData
import woowacourse.shopping.data.shoppingCart.dao.ShoppingCartDao
import woowacourse.shopping.data.shoppingCart.database.ShoppingCartDatabase

object LocalShoppingCartDataSource : ShoppingCartDataSource {
    private lateinit var dao: ShoppingCartDao

    fun init(applicationContext: Context) {
        val db =
            Room
                .databaseBuilder(
                    applicationContext,
                    ShoppingCartDatabase::class.java,
                    "shoppingCart",
                ).build()

        dao = db.dao()
    }

    override fun pageableCartItems(
        page: Int,
        size: Int,
    ): PageableCartItemData = TODO()

    override fun addCartItem(
        productId: Long,
        quantity: Int,
    ) {
        TODO()
    }

    override fun remove(cartItem: CartItemEntity) = dao.remove(cartItem)

    override fun update(cartItems: List<CartItemEntity>) = dao.replaceAll(cartItems)

    override fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun cartItemsSize(): Int =
        dao.quantityOf(
            productId = TODO(),
        )
}

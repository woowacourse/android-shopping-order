package woowacourse.shopping.database.cart

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository

class CartDatabase(
    private val shoppingDb: SQLiteDatabase,
) : CartRepository {
    /*override fun getAllProductInCart(): List<CartProduct> {
        val cartProducts = mutableListOf<CartProduct>()
        getCartCursor().use {
            while (it.moveToNext()) {
                cartProducts.add(getCartProduct(it))
            }
        }
        return cartProducts
    }*/

    @SuppressLint("Range")
    private fun getCartProduct(cursor: Cursor): CartProduct {
        return CartProduct(1, 5, Product(1, "", 1, ""))
    }

    override fun getAllProductInCart(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun insert(id: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateCount(
        id: Long,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun remove(id: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long, onSuccess: (CartProduct?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getSubList(offset: Int, step: Int, onSuccess: (List<CartProduct>) -> Unit) {
        TODO("Not yet implemented")
    }

    /* override fun insert(product: CartProduct) {
         val values = ContentValues().apply {
             put(CartConstant.TABLE_COLUMN_PRODUCT_ID, product.product.id)
             put(CartConstant.TABLE_COLUMN_PRODUCT_NAME, product.product.name)
             put(CartConstant.TABLE_COLUMN_PRODUCT_PRICE, product.product.price)
             put(CartConstant.TABLE_COLUMN_PRODUCT_IMAGE_URL, product.product.imageUrl)

             val existingProduct = findById(product.product.id)
             val count = if (existingProduct != null) {
                 existingProduct.count + product.count
             } else {
                 product.count
             }
             put(CartConstant.TABLE_COLUMN_CART_PRODUCT_COUNT, count)
             put(CartConstant.TABLE_COLUMN_CART_PRODUCT_IS_CHECKED, true)
             put(CartConstant.TABLE_COLUMN_PRODUCT_SAVE_TIME, System.currentTimeMillis())
         }
         shoppingDb.insertWithOnConflict(
             CartConstant.TABLE_NAME,
             null,
             values,
             SQLiteDatabase.CONFLICT_REPLACE,
         )
     }*/

    /* override fun getSubList(offset: Int, size: Int): List<CartProduct> {
         val allProducts = getAllProductInCart().ifEmpty { emptyList() }
         val lastIndex = allProducts.lastIndex
         val endIndex = (lastIndex + 1).coerceAtMost(offset + size)
         if (offset < 0) {
             return if (size > 0 && size <= lastIndex + 1) {
                 getAllProductInCart().subList(0, size)
             } else {
                 getAllProductInCart()
             }
         }

         return if (offset <= lastIndex) getAllProductInCart().subList(offset, endIndex) else emptyList()
     }

     override fun remove(id: Long) {
         val query =
             "DELETE FROM ${CartConstant.TABLE_NAME} WHERE ${CartConstant.TABLE_COLUMN_PRODUCT_ID} = $id"
         shoppingDb.execSQL(query)
     }

     override fun updateCount(id: Long, count: Int) {
         val query =
             "UPDATE ${CartConstant.TABLE_NAME} SET ${CartConstant.TABLE_COLUMN_CART_PRODUCT_COUNT} = $count WHERE  ${CartConstant.TABLE_COLUMN_PRODUCT_ID} = $id"
         shoppingDb.execSQL(query)
     }

     override fun getAllProductInCart(
         onSuccess: (List<CartProduct>) -> Unit,
         onFailure: (Exception) -> Unit
     ) {
         TODO("Not yet implemented")
     }
 */
    /*  override fun insert(
          cartProduct: CartProduct,
          onSuccess: (String) -> Unit,
          onFailure: (Exception) -> Unit,
      ) {
          TODO("Not yet implemented")
      }

      override fun remove(id: Long, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
          TODO("Not yet implemented")
      }

      override fun updateCount(
          id: Long,
          count: Int,
          onSuccess: (String) -> Unit,
          onFailure: (Exception) -> Unit,
      ) {
          TODO("Not yet implemented")
      }

      override fun findById(id: Long): CartProduct? {
          var cartProduct: CartProduct? = null
          findByIdCursor(id).use {
              if (it.moveToFirst()) {
                  cartProduct = getCartProduct(it)
              }
          }
          return cartProduct
      }

      override fun getSubList(offset: Int, step: Int): List<CartProduct> {
          TODO("Not yet implemented")
      }

      private fun findByIdCursor(id: Long): Cursor {
          val query =
              "SELECT * FROM ${CartConstant.TABLE_NAME} WHERE ${CartConstant.TABLE_COLUMN_PRODUCT_ID} = $id"
          return shoppingDb.rawQuery(query, null)
      }

      private fun getCartCursor(): Cursor {
          val query =
              "SELECT * FROM ${CartConstant.TABLE_NAME} ORDER BY ${CartConstant.TABLE_COLUMN_PRODUCT_SAVE_TIME}"
          return shoppingDb.rawQuery(query, null)
      }*/
}

package woowacourse.shopping.data.database.product

import android.content.Context
import woowacourse.shopping.data.database.ShoppingDBHelper
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository

class ProductDatabase(context: Context) : ProductRepository {
    private val db = ShoppingDBHelper(context).writableDatabase

    private var offset = 0
    override fun getAll(callback: (List<Product>?) -> Unit) {
        val products = mutableListOf<Product>()
        db.rawQuery(ProductConstant.getGetAllQuery(), null).use {
            while (it.moveToNext()) {
                products.add(ProductConstant.fromCursor(it))
            }
        }
        callback(products)
    }

    override fun getNext(count: Int, callback: (List<Product>?) -> Unit) {
        val products = mutableListOf<Product>()
        db.rawQuery(ProductConstant.getGetNextQuery(count, offset), null).use {
            while (it.moveToNext()) {
                products.add(ProductConstant.fromCursor(it))
                offset++
            }
        }
        callback(products)
    }

    override fun findById(id: Int, callback: (Product?) -> Unit) {
        db.rawQuery(ProductConstant.getGetQuery(id), null).use {
            it.moveToNext()
            callback(ProductConstant.fromCursor(it))
        }
    }
}

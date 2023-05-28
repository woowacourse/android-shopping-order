package woowacourse.shopping.database.product

import android.content.Context
import woowacourse.shopping.data.localDataSource.ProductLocalDataSource
import woowacourse.shopping.database.ShoppingDBHelper
import woowacourse.shopping.database.cart.ProductConstant
import woowacourse.shopping.model.Product

class ProductSqliteDataSource(context: Context) : ProductLocalDataSource {
    private val db = ShoppingDBHelper(context).writableDatabase

    private var offset = 0
    override fun getAll(callback: (Result<List<Product>>) -> Unit) {
        val products = mutableListOf<Product>()
        db.rawQuery(ProductConstant.getGetAllQuery(), null).use {
            while (it.moveToNext()) {
                products.add(ProductConstant.fromCursor(it))
            }
        }
        callback(Result.success(products))
    }

    override fun getNext(count: Int, callback: (Result<List<Product>>) -> Unit) {
        val products = mutableListOf<Product>()
        db.rawQuery(ProductConstant.getGetNextQuery(count, offset), null).use {
            while (it.moveToNext()) {
                products.add(ProductConstant.fromCursor(it))
                offset++
            }
        }
        callback(Result.success(products))
    }

    override fun findById(id: Int, callback: (Result<Product>) -> Unit) {
        db.rawQuery(ProductConstant.getGetQuery(id), null).use {
            it.moveToNext()
            callback(Result.success(ProductConstant.fromCursor(it)))
        }
    }

    override fun clear() {
        db.execSQL(ProductConstant.getClearQuery())
        offset = 0
    }

    override fun insertAll(it: List<Product>) {
        it.forEach { product ->
            db.execSQL(ProductConstant.getInsertQuery(product))
        }
    }

    override fun isCached(): Boolean {
        return db.rawQuery(ProductConstant.getGetAllQuery(), null).use {
            it.count > 0
        }
    }

    override fun isEndOfCache(): Boolean {
        return db.rawQuery(ProductConstant.getGetNextQuery(1, offset), null).use {
            it.count == 0
        }
    }

    override fun insert(product: Product, callback: (Int) -> Unit) {
        db.execSQL(ProductConstant.getInsertQuery(product))
        callback(product.id)
    }
}

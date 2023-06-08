package woowacourse.shopping.data.local.product

import android.content.Context
import woowacourse.shopping.data.local.ProductLocalDataSource
import woowacourse.shopping.data.local.ShoppingDBHelper
import woowacourse.shopping.model.Product

class ProductSqliteDataSource(context: Context) : ProductLocalDataSource {
    private val db = ShoppingDBHelper(context).writableDatabase

    private var offset = 0
    override fun getAll(): Result<List<Product>> {
        val products = mutableListOf<Product>()
        db.rawQuery(ProductConstant.getGetAllQuery(), null).use {
            while (it.moveToNext()) {
                products.add(ProductConstant.fromCursor(it))
            }
        }
        return Result.success(products)
    }

    override fun getNext(count: Int): Result<List<Product>> {
        val products = mutableListOf<Product>()
        db.rawQuery(ProductConstant.getGetNextQuery(count, offset), null).use {
            while (it.moveToNext()) {
                products.add(ProductConstant.fromCursor(it))
                offset++
            }
        }
        return Result.success(products)
    }

    override fun findById(id: Int): Result<Product> {
        runCatching {
            db.rawQuery(ProductConstant.getGetQuery(id), null).use {
                it.moveToNext()
                return Result.success(ProductConstant.fromCursor(it))
            }
        }.onFailure { throwable ->
            return Result.failure(throwable)
        }
        return Result.failure(IllegalStateException())
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

    override fun insert(product: Product): Result<Int> {
        runCatching { db.execSQL(ProductConstant.getInsertQuery(product)) }
            .onFailure { throwable -> return Result.failure(throwable) }
        return Result.success(product.id)
    }
}

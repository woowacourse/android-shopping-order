package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

interface LocalCartDataSource {
    fun modifyQuantity(
        product: Product,
        quantityDelta: Int,
    ): Result<Long>

    fun setQuantity(
        product: Product,
        newQuantityValue: Int,
    ): Result<Long>

    fun deleteProduct(product: Product): Result<Long>

    fun find(product: Product): Result<Cart>

    fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<List<Cart>>

    fun loadAll(): Result<List<Cart>>

    fun getMaxPage(pageSize: Int): Result<Int>
}

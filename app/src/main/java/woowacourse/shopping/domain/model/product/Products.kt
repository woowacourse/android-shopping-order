package woowacourse.shopping.domain.model.product

import android.R.attr.category

class Products(
    val items: List<Product>,
    val isLast: Boolean,
) {
    fun getCategoryProductsLimit(cartProducts: Set<Product>, recommended: Product): List<Product> = items.filter { it !in cartProducts && it != recommended }.take(PAGE_SIZE)

    companion object{
        const val PAGE_SIZE = 10
    }
}

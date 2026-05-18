package woowacourse.shopping.domain.model.product

class Products(
    val items: List<Product>,
    val isLast: Boolean,
) {
    fun getCategoryProducts(category: String): List<Product> = items.filter { it.category.value == category }.take(10)
}

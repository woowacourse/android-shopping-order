package woowacourse.shopping.model.product

class Products(
    products: List<Product>,
) : Iterable<Product> {
    private val value = products.toList()

    override fun iterator(): Iterator<Product> = value.iterator()
}

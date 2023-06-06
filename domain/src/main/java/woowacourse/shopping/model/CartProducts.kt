package woowacourse.shopping.model

class CartProducts(cartProducts: List<CartProduct> = listOf()) {
    private val _items = cartProducts.toMutableList()
    val items get() = _items.toList()

    val size get(): Int = _items.size
    fun isProductSelectedByRange(startIndex: Int, productSize: Int): Boolean {
        val minSize = minOf(size, startIndex + productSize)
        return (startIndex until minSize).all { _items[it].isChecked }
    }

    fun getProductsInRange(startIndex: Int, productSize: Int): CartProducts {
        val minSize = minOf(size, startIndex + productSize)
        return CartProducts(_items.subList(startIndex, minSize))
    }

    fun addProducts(products: List<CartProduct>) {
        _items.addAll(products)
    }

    fun addProductByCount(product: Product, count: Int) {
        val targetIndex = _items.indexOfLast { it.product == product }
        addTargetProductCount(targetIndex, count)
    }

    private fun addTargetProductCount(targetIndex: Int, count: Int) {
        val targetProduct = _items[targetIndex]
        _items[targetIndex] = targetProduct + count
    }

    fun deleteProduct(product: Product) {
        _items.removeIf { it.product == product }
    }

    fun subProductByCount(product: Product, count: Int) {
        val targetIndex = _items.indexOfLast { it.product == product }
        if (targetIndex == NOT_FOUND) {
            return
        }
        subTargetProductCount(targetIndex, count)
    }

    private fun subTargetProductCount(targetIndex: Int, count: Int) {
        val targetProduct = _items[targetIndex]
        if (targetProduct.quantity - count < MIN_COUNT) {
            return
        }
        _items[targetIndex] = targetProduct - count
    }

    fun changeSelectedProduct(product: Product) {
        val targetIndex = _items.indexOfLast { it.product == product }
        val targetProduct = _items[targetIndex]
        _items[targetIndex] = targetProduct.select(!targetProduct.isChecked)
    }

    fun selectProductsRange(isSelect: Boolean, startIndex: Int, productSize: Int) {
        val minSize = minOf(startIndex + productSize, size)
        (startIndex until minSize).forEach { _items[it] = _items[it].select(isSelect) }
    }

    fun getSelectedProductsPrice(): Int {
        return _items.filter { it.isChecked }.sumOf { it.getTotalPrice() }
    }

    fun getSelectedProductsTotalCount(): Int {
        return _items.filter { it.isChecked }.sumOf { it.quantity }
    }

    fun getSelectedCartIds(): List<Long> {
        return _items.filter { it.isChecked }.map { it.cartId }
    }

    companion object {
        private const val NOT_FOUND = -1
        private const val MIN_COUNT = 1
    }
}

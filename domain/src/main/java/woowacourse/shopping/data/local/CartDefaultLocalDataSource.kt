package woowacourse.shopping.data.local

import java.lang.Integer.min
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProductPage

class CartDefaultLocalDataSource : CartLocalDataSource {
    private val cartProducts = mutableListOf<CartProduct>()

    override fun getAll(): Result<List<CartProduct>> {
        return Result.success(cartProducts)
    }

    override fun getPage(offset: Int, size: Int): Result<CartProductPage> {
        val page = CartProductPage(
            subList(offset, size),
            hasPrevPage(offset, size),
            hasNextPage(offset, size),
            getCurrentPage(offset, size)
        )
        return Result.success(page)
    }

    override fun getChecked(): Result<List<CartProduct>> {
        return Result.success(cartProducts.filter { it.checked })
    }

    override fun getCheckCount(ids: List<Int>): Int {
        return cartProducts.filter { ids.contains(it.id) }.count { it.checked }
    }

    override fun getTotalQuantity(): Int {
        return cartProducts.sumOf { it.quantity }
    }

    override fun getTotalCheckedQuantity(): Int {
        return cartProducts.filter { it.checked }.sumOf { it.quantity }
    }

    override fun getTotalCheckedPrice(): Int {
        return cartProducts.filter { it.checked }.sumOf { it.product.price * it.quantity }
    }

    private fun getCurrentPage(offset: Int, size: Int): Int {
        return offset / size + 1
    }

    private fun hasNextPage(offset: Int, size: Int): Boolean {
        return offset + size < cartProducts.size
    }

    private fun hasPrevPage(offset: Int, size: Int): Boolean {
        return offset > 0
    }

    override fun updateChecked(ids: List<Int>, checked: Boolean) {
        ids.forEach { id ->
            val idx = cartProducts.indexOfFirst { it.id == id }
            if (idx == -1) { return }
            cartProducts[idx] = cartProducts[idx].copy(checked = checked)
        }
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        val idx = cartProducts.indexOfFirst { it.id == id }
        if (idx == -1) { return }
        cartProducts[idx] = cartProducts[idx].copy(checked = checked)
    }

    override fun replaceAll(cartProducts: List<CartProduct>) {
        val newValue = cartProducts.map { cartProduct ->
            cartProduct.copy(
                checked = this.cartProducts.find { it.id == cartProduct.id }?.checked ?: true
            )
        }
        this.cartProducts.clear()
        this.cartProducts.addAll(newValue)
    }

    override fun getByProductId(productId: Int): Result<CartProduct> {
        val index = cartProducts.indexOfFirst { it.product.id == productId }
        if (index == -1) { return Result.failure(Throwable("Failed to get by product id")) }
        return Result.success(cartProducts[index])
    }

    private fun subList(offset: Int, size: Int): List<CartProduct> {
        return cartProducts.subList(offset, min(offset + size, cartProducts.size))
    }
}

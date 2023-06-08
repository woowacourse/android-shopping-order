package woowacourse.shopping.data.local

import java.lang.Integer.min
import woowacourse.shopping.model.CartProduct

class CartDefaultLocalDataSource : CartLocalDataSource {
    private val cartProducts = mutableListOf<CartProduct>()
    private var offset = 0
    private var size = 5

    override fun getAll(): Result<List<CartProduct>> {
        return Result.success(cartProducts)
    }

    override fun getPage(offset: Int, size: Int): Result<List<CartProduct>> {
        this.offset = offset
        this.size = size
        return Result.success(subList(offset, size))
    }

    override fun getCurrentPage(): Int {
        return offset / size + 1
    }

    override fun getCurrentPageChecked(): Int {
        return subList(offset, size).count { it.checked }
    }

    override fun getChecked(): Result<List<CartProduct>> {
        return Result.success(cartProducts.filter { it.checked })
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

    override fun hasNextPage(): Boolean {
        return offset + size < cartProducts.size
    }

    override fun hasPrevPage(): Boolean {
        return offset > 0
    }

    override fun updateCurrentPageChecked(checked: Boolean) {
        subList(offset, size).forEachIndexed { index, cartProduct ->
            cartProducts[offset + index] = cartProduct.copy(checked = checked)
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

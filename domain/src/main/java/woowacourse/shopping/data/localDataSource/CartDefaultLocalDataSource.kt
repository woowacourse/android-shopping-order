package woowacourse.shopping.data.localDataSource

import java.lang.Integer.min
import woowacourse.shopping.model.CartProduct

class CartDefaultLocalDataSource : CartLocalDataSource {
    private val cartProducts = mutableListOf<CartProduct>()
    private var offset = 0
    private var size = 5

    override fun getPage(offset: Int, size: Int): Result<List<CartProduct>> {
        this.offset = offset
        this.size = size
        return Result.success(cartProducts.subList(offset, min(offset + size, cartProducts.size)))
    }

    override fun hasNextPage(): Boolean {
        return offset + size < cartProducts.size
    }

    override fun hasPrevPage(): Boolean {
        return offset > 0
    }

    override fun getTotalCount(): Int {
        return cartProducts.sumOf { it.quantity }
    }

    override fun getTotalCheckedCount(): Int {
        return cartProducts.filter { it.checked }.sumOf { it.quantity }
    }

    override fun getTotalPrice(): Int {
        return cartProducts.filter { it.checked }.sumOf { it.product.price * it.quantity }
    }

    override fun setCurrentPageChecked(checked: Boolean) {
        cartProducts.subList(offset, min(offset + size, cartProducts.size))
            .forEachIndexed { index, cartProduct ->
                cartProducts[offset + index] = cartProduct.copy(checked = checked)
            }
    }

    override fun getCurrentPageChecked(): Int {
        return cartProducts.subList(offset, min(offset + size, cartProducts.size)).count {
            it.checked
        }
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

    override fun updateChecked(id: Int, checked: Boolean) {
        cartProducts.indexOfFirst { it.id == id }.let {
            if (it == -1) { return }
            cartProducts[it] = cartProducts[it].copy(checked = checked)
        }
    }

    override fun getByProductId(productId: Int): Result<CartProduct> {
        val index = cartProducts.indexOfFirst { it.product.id == productId }
        if (index == -1) {
            return Result.failure(Throwable("Failed to get by product id"))
        }
        return Result.success(cartProducts[index])
    }

    override fun getCurrentPage(): Int {
        return offset / size + 1
    }

    override fun getChecked(): Result<List<CartProduct>> {
        return Result.success(cartProducts.filter { it.checked })
    }
}

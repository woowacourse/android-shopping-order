package woowacourse.shopping.data.cache

import com.example.domain.model.CartProduct
import com.example.domain.model.Product

object CartCache {
    private val _cartProducts = mutableListOf<CartProduct>()
    val cartProducts: List<CartProduct>
        get() = _cartProducts.toList()

    fun addCartProducts(cartProducts: List<CartProduct>) {
        _cartProducts.addAll(cartProducts)
    }

    fun updateSelection(product: Product, isSelected: Boolean) {
        val index = _cartProducts.indexOfFirst { product == it.product }
        if (index != -1) {
            _cartProducts[index] = _cartProducts[index].copy(isSelected = isSelected)
        }
    }

    fun getCartProductsByPage(page: Int, size: Int): List<CartProduct> {
        val startIndex = (page - 1) * size
        val endIndex =
            if (startIndex + size >= _cartProducts.size) _cartProducts.size
            else startIndex + size

        return _cartProducts.subList(startIndex, endIndex)
    }

    fun clear() {
        _cartProducts.clear()
    }
}

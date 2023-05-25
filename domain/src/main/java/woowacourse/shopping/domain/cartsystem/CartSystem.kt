package woowacourse.shopping.domain.cartsystem

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Price

class CartSystem {
    private val _selectedProducts = mutableListOf<CartProduct>()
    val selectedProducts: List<CartProduct>
        get() = _selectedProducts.toList()
    private var totalPrice: Price = Price(0)

    fun isSelectedProduct(product: CartProduct): Boolean {
        return _selectedProducts.find { it.product.id == product.cartId } != null
    }

    fun selectProduct(cartProduct: CartProduct): CartSystemResult {
        if (_selectedProducts.find { it.product.id == cartProduct.product.id } == null) {
            val price = cartProduct.product.price
            if (price != null) {
                _selectedProducts.add(cartProduct)
                totalPrice += price * cartProduct.count
            }
            return CartSystemResult(totalPrice.price, _selectedProducts.size)
        }
        return deselectProduct(cartProduct.product.id)
    }

    private fun deselectProduct(id: Int): CartSystemResult {
        val product = _selectedProducts.find { it.product.id == id }
            ?: throw java.lang.IllegalArgumentException()
        totalPrice -= product.product.price * product.count
        _selectedProducts.remove(product)
        return CartSystemResult(totalPrice.price, _selectedProducts.size)
    }

    fun updateProduct(id: Int, count: Int): CartSystemResult {
        val index = _selectedProducts.indexOfFirst { it.product.id == id }
        if (index != -1) {
            val diff = count - _selectedProducts[index].count
            totalPrice += _selectedProducts[index].product.price.price * diff
            _selectedProducts[index] = _selectedProducts[index].copy(count = count)
        }
        return CartSystemResult(totalPrice.price, _selectedProducts.size)
    }

    fun removeProduct(id: Int): CartSystemResult {
        val item = _selectedProducts.find { it.product.id == id }
        if (item != null) {
            totalPrice -= item.product.price * item.count
            _selectedProducts.remove(item)
        }
        return CartSystemResult(totalPrice.price, _selectedProducts.size)
    }
}

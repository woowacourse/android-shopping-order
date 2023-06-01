package woowacourse.shopping.domain.cartsystem

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Price

class CartSystem {
    private val _selectedProducts = mutableListOf<CartProduct>()
    val selectedProducts: List<CartProduct>
        get() = _selectedProducts.toList()
    private var totalPrice: Price = Price(0)

    fun isSelectedProduct(cartProduct: CartProduct): Boolean {
        return _selectedProducts.find { it.id == cartProduct.id } != null
    }

    fun selectProduct(cartProduct: CartProduct): CartSystemResult {
        if (_selectedProducts.find { it.id == cartProduct.id } == null) {
            val price = cartProduct.product.price
            _selectedProducts.add(cartProduct)
            totalPrice += price * cartProduct.quantity
            return CartSystemResult(totalPrice.price, _selectedProducts.size)
        }
        return deselectProduct(cartProduct.id)
    }

    private fun deselectProduct(id: Int): CartSystemResult {
        val product = _selectedProducts.find { it.id == id }
            ?: throw java.lang.IllegalArgumentException()
        totalPrice -= product.product.price * product.quantity
        _selectedProducts.remove(product)
        return CartSystemResult(totalPrice.price, _selectedProducts.size)
    }

    fun updateProduct(id: Int, count: Int): CartSystemResult {
        val index = _selectedProducts.indexOfFirst { it.id == id }
        if (index != -1) {
            val diff = count - _selectedProducts[index].quantity
            totalPrice += _selectedProducts[index].product.price.price * diff
            _selectedProducts[index] = _selectedProducts[index].copy(quantity = count)
        }
        return CartSystemResult(totalPrice.price, _selectedProducts.size)
    }

    fun removeProduct(id: Int): CartSystemResult {
        val item = _selectedProducts.find { it.id == id }
        if (item != null) {
            totalPrice -= item.product.price * item.quantity
            _selectedProducts.remove(item)
        }
        return CartSystemResult(totalPrice.price, _selectedProducts.size)
    }
}

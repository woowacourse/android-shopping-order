package woowacourse.shopping.feature

import com.example.domain.model.CartProduct
import com.example.domain.model.Price
import com.example.domain.model.Product

object ProductFixture {

    fun makeCartProducts(cartProductsIds: List<Long>, price: Int, count: Int): List<CartProduct> {
        return cartProductsIds.map { id ->
            makeCartProduct(id, price, count)
        }
    }

    fun makeCartProduct(cartProductId: Long, price: Int, count: Int): CartProduct {
        val product = Product(1, "", "", Price(price))
        return CartProduct(cartProductId, product, count, false)
    }
}

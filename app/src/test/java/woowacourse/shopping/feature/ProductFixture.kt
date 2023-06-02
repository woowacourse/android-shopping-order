package woowacourse.shopping.feature

import com.example.domain.model.CartProduct
import com.example.domain.model.Price
import com.example.domain.model.Product

object ProductFixture {
    fun makeProduct(price: Int): Product {
        return Product(1, "", "", Price(price))
    }

    fun makeCartProducts(product: Product, size: Int): List<CartProduct> {
        return List(size) { CartProduct(1, product, 1, false) }
    }
}

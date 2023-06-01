package woowacourse.shopping.feature

import com.example.domain.model.CartProduct
import com.example.domain.model.Price
import com.example.domain.model.Product

object ProductFixture {
    val products = listOf(Product(1, "", "", Price(1)))
    val cartProducts = listOf(CartProduct(1, products[0], 0, false))
}

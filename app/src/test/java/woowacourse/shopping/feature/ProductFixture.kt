package woowacourse.shopping.feature

import com.example.domain.model.Price
import com.example.domain.model.Product

object ProductFixture {

    fun getProducts(vararg idToPrice: Pair<Long, Int>): List<Product> {
        return idToPrice.map { Product(it.first, "", "", it.second) }
    }
}

private fun Product(id: Long, name: String, imgUrl: String, price: Int): Product {
    return Product(id, name, imgUrl, Price(price))
}

internal fun Product(id: Long, price: Int): Product {
    return Product(id, "", "", price)
}

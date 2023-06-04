package woowacourse.shopping.feature

import com.example.domain.model.CartProduct
import com.example.domain.model.Product

internal object CartFixture {
    fun getMockCarts(vararg idProductCount: Triple<Long, Product, Int>): List<CartProduct> {
        return idProductCount.map {
            CartProduct(it.first, it.second, it.third, true)
        }
    }
}

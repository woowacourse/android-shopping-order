package woowacourse.shopping.presenter

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

object CartProductsFixture {
    val cartProducts = listOf(
        CartProduct(1, 1, Product(0, "락토핏", Price(10000), "")),
        CartProduct(2, 1, Product(1, "현미밥", Price(10000), "")),
        CartProduct(3, 1, Product(2, "헛개차", Price(10000), "")),
        CartProduct(4, 1, Product(3, "헛개차", Price(10000), "")),
        CartProduct(5, 1, Product(4, "헛개차", Price(10000), "")),
        CartProduct(6, 1, Product(5, "헛개차", Price(10000), "")),
        CartProduct(7, 1, Product(6, "헛개차", Price(10000), "")),
        CartProduct(8, 1, Product(7, "헛개차", Price(10000), "")),
        CartProduct(9, 1, Product(8, "헛개차", Price(10000), "")),
        CartProduct(10, 1, Product(9, "헛개차", Price(10000), "")),
        CartProduct(11, 1, Product(10, "헛개차", Price(10000), "")),
        CartProduct(12, 1, Product(11, "헛개차", Price(10000), "")),
    )
}

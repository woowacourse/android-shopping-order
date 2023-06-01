package woowacourse.shopping.domain.model.discount

import woowacourse.shopping.domain.model.Price

abstract class Discountable(val value: Int) {
    abstract fun discountable(other: Discountable): Boolean
    abstract fun discount(price: Price): Price
}

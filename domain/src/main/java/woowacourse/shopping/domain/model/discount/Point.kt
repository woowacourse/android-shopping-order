package woowacourse.shopping.domain.model.discount

import woowacourse.shopping.domain.model.Price

class Point(value: Int) : Discountable(value), Comparable<Point> {

    override fun discountable(other: Discountable): Boolean {
        if (other !is Point) return false
        if (value == 0) return false

        return value >= other.value
    }

    override fun discount(price: Price): Price {
        return price - value
    }

    override fun compareTo(other: Point): Int {
        return value - other.value
    }
}

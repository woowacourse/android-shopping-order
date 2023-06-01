package woowacourse.shopping.domain.model

data class Point(val value: Int) : Comparable<Point> {

    override fun compareTo(other: Point): Int {
        return value - other.value
    }
}

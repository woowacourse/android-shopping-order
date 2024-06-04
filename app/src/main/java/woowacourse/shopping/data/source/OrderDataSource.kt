package woowacourse.shopping.data.source

interface OrderDataSource {
    fun orderItems(ids: List<Int>): Result<Unit>
}

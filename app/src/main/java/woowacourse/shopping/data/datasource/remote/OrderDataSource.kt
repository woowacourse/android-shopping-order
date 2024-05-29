package woowacourse.shopping.data.datasource.remote

interface OrderDataSource {
    fun postOrder(cartItemsIds: List<Int>): Result<Unit>
}

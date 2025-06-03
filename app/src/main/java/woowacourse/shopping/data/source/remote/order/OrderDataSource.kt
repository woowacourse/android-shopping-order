package woowacourse.shopping.data.source.remote.order

interface OrderDataSource {
    fun orderCheckedItems(
        cartIds: List<Long>,
        onResult: (Result<Unit>) -> Unit,
    )
}

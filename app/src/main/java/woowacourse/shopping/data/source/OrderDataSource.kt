package woowacourse.shopping.data.source

interface OrderDataSource {
    fun order(cartItemIds: List<Long>)
}
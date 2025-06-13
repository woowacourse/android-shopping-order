package woowacourse.shopping.data.source

interface OrderDataSource {
    suspend fun orderProducts(
        orderProducts: List<Int>
    ): Boolean
}

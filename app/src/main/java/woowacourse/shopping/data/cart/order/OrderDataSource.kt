package woowacourse.shopping.data.cart.order

interface OrderDataSource {
    suspend fun orderProducts(productIds: List<Long>): Result<Unit>
}

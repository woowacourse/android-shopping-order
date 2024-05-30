package woowacourse.shopping.data.cart.order

interface OrderDataSource {
    fun orderProducts(productIds: List<Long>): Result<Unit>
}

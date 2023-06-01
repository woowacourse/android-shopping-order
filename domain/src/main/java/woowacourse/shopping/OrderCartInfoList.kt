package woowacourse.shopping

class OrderCartInfoList(private val orderCartInfos: List<OrderCartInfo>) {
    fun getTotalPrice(): Int {
        return orderCartInfos.sumOf { it.product.price.value * it.count }
    }
}

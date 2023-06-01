package woowacourse.shopping

class OrderCartInfoList(orderCartInfos: List<OrderCartInfo>) {
    val value = orderCartInfos.toList()
    fun getTotalPrice(): Int {
        return value.sumOf { it.product.price.value * it.count }
    }
}

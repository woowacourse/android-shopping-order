package woowacourse.shopping

class OrderProducts(list: List<OrderProduct>) {
    private val _items = list.toMutableList()
    val items get() = _items.toList()
}

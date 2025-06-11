package woowacourse.shopping.view.util.product

interface QuantityControlEventHandler {
    fun onQuantityIncreaseClick(id: Int)

    fun onQuantityDecreaseClick(id: Int)
}

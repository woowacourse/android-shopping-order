package woowacourse.shopping.view.util.product

interface QuantityControlEventHandler<T> {
    fun onQuantityIncreaseClick(item: T)

    fun onQuantityDecreaseClick(item: T)
}

package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.PageNumber

class CartPagination(productInCarts: List<ProductInCart>) {
    private val _shoppingCart = mutableListOf<ProductInCart>()
    val shoppingCart: List<ProductInCart> get() = _shoppingCart.toList()
    var currentPage = PageNumber()
        private set
    private val pageStartIdx: Int get() = (currentPage.value - 1) * PAGE_UNIT
    private val pageEndIdx: Int
        get() {
            val toIndex = currentPage.value * PAGE_UNIT - 1
            return if (toIndex >= shoppingCart.size) (shoppingCart.size - 1) else toIndex
        }

    init {
        _shoppingCart.addAll(productInCarts)
    }

    private fun getAbsolutePosition(index: Int): Int {
        return (currentPage.value - 1) * PAGE_UNIT + index
    }

    fun getCurrentPage(): List<ProductInCart> {
        return if (shoppingCart.isEmpty()) {
            emptyList()
        } else {
            shoppingCart.slice(IntRange(pageStartIdx, pageEndIdx))
        }
    }

    fun goNextPage() {
        currentPage = currentPage.nextPage()
    }

    fun goPreviousPage() {
        currentPage = currentPage.previousPage()
    }

    fun isNextPageEnable(): Boolean = shoppingCart.size > currentPage.value * PAGE_UNIT
    fun isPreviousPageEnable(): Boolean = currentPage.value > 1

    operator fun get(index: Int): ProductInCart = shoppingCart[getAbsolutePosition(index)]

    fun removeFromCurrentPage(index: Int) {
        _shoppingCart.removeAt(getAbsolutePosition(index))
    }

    fun isCurrentPageEmpty(): Boolean {
        if (shoppingCart.isEmpty()) return false
        if (shoppingCart.size <= pageStartIdx) {
            currentPage = currentPage.previousPage()
            return true
        }
        return false
    }

    fun checkAll(isChecked: Boolean) {
        for (idx in pageStartIdx..pageEndIdx) {
            _shoppingCart[idx] = shoppingCart[idx].copy(isChecked = isChecked)
        }
    }

    fun isAllChecked(): Boolean {
        return shoppingCart
            .slice(IntRange(pageStartIdx, pageEndIdx))
            .all { it.isChecked }
    }

    fun changeCheck(index: Int, isChecked: Boolean) {
        val position = getAbsolutePosition(index)
        _shoppingCart[position] = shoppingCart[position].copy(isChecked = isChecked)
    }

    fun getPayment(): Int {
        return shoppingCart.asSequence()
            .filter { it.isChecked }
            .sumOf { it.quantity * it.product.price }
    }

    fun getOrderCount(): Int {
        return shoppingCart.asSequence()
            .filter { it.isChecked }
            .sumOf { it.quantity }
    }

    fun updateProductQuantity(index: Int, operator: Operator) {
        val position = getAbsolutePosition(index)
        _shoppingCart[position] = operator.operate(shoppingCart[position])
    }

    companion object {
        private const val PAGE_UNIT = 5
    }
}

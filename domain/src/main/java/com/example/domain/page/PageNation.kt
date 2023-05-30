package com.example.domain.page

import com.example.domain.cart.CartProduct
import com.example.domain.cart.CartProducts
import kotlin.math.ceil

data class PageNation(
    private val cartProducts: CartProducts,
    val currentPage: Int,
) {
    val pageCount: Int
        get() = calculateTotalPageCount(cartProducts.size)

    val allSize: Int
        get() = cartProducts.size

    val allList: List<CartProduct>
        get() = cartProducts.all

    val totalCheckedPrice: Int
        get() = cartProducts.totalCheckedPrice

    val checkedCount: Int
        get() = allList.count { it.checked }

    val isAnyChecked: Boolean
        get() = allList.any { it.checked }

    val currentPageCartProducts: List<CartProduct>
        get() {
            val fromIndex = (currentPage - 1) * LOAD_ITEM_SIZE_PER_PAGE
            var toIndex = currentPage * LOAD_ITEM_SIZE_PER_PAGE
            if (toIndex > allSize) toIndex = allSize
            if (toIndex == fromIndex) return emptyList()
            return allList.subList(fromIndex, toIndex)
        }

    fun hasNextPage(): Boolean {
        return currentPage < pageCount
    }

    fun hasPreviousPage(): Boolean {
        return currentPage > 1
    }

    fun nextPage(): PageNation {
        if (hasNextPage().not()) return this
        return copy(currentPage = this.currentPage + 1)
    }

    fun previousPage(): PageNation {
        if (hasPreviousPage().not()) return this
        return copy(currentPage = this.currentPage - 1)
    }

    fun remove(cartId: Long): PageNation {
        val newCartProducts = cartProducts.remove(cartId)
        val newSize = newCartProducts.size
        val newPageTotalCount = calculateTotalPageCount(newSize)
        if (currentPage > newPageTotalCount) return PageNation(newCartProducts, currentPage - 1)
        return copy(cartProducts = newCartProducts)
    }

    fun updateCurrentPageCheckedAll(isChecked: Boolean): PageNation {
        val currentPageCartProductsId = currentPageCartProducts.map { it.id }
        return copy(cartProducts = cartProducts.updateAllCheckedBy(currentPageCartProductsId, isChecked))
    }

    fun updateCountState(cartId: Long, count: Int): PageNation {
        val newCartProduct = cartProducts.changeCount(cartId, count)
        return copy(cartProducts = newCartProduct)
    }

    private fun calculateTotalPageCount(size: Int): Int {
        if (size == 0) return 1
        return ceil(size.toDouble() / LOAD_ITEM_SIZE_PER_PAGE).toInt()
    }

    companion object {
        private const val LOAD_ITEM_SIZE_PER_PAGE = 5
    }
}

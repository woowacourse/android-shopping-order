package woowacourse.shopping.ui.productlist

class ProductPageStateHolder(
    initialPage: Int = 0,
) {
    private val basePage: Int = initialPage.coerceAtLeast(0)
    private var nextPage: Int = basePage
    private var canLoadNextPage: Boolean = true
    private val loadedProductIds: MutableList<Long> = mutableListOf()
    private val loadedProductIdSet: MutableSet<Long> = mutableSetOf()

    fun reset(startPage: Int = basePage) {
        nextPage = startPage.coerceAtLeast(basePage)
        canLoadNextPage = true
        loadedProductIds.clear()
        loadedProductIdSet.clear()
    }

    fun peekNextPage(): Int = nextPage

    fun canLoadNextPage(): Boolean = canLoadNextPage

    fun displayedProductIds(): List<Long> = loadedProductIds.toList()

    fun onPageLoaded(
        productIds: List<Long>,
        hasNextPage: Boolean,
        replaceExisting: Boolean,
    ) {
        if (replaceExisting) {
            loadedProductIds.clear()
            loadedProductIdSet.clear()
        }

        productIds.forEach { productId ->
            if (loadedProductIdSet.add(productId)) {
                loadedProductIds += productId
            }
        }

        canLoadNextPage = hasNextPage
        if (hasNextPage) {
            nextPage += 1
        }
    }
}

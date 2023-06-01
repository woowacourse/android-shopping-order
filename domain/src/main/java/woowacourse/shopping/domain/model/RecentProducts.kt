package woowacourse.shopping.domain.model

data class RecentProducts(
    private val items: List<RecentProduct> = emptyList(),
    private val maxCount: Int = 10,
) {

    fun getItems(): List<RecentProduct> = items.take(maxCount).map { it.copy() }

    fun getLatest(): RecentProduct? = items.firstOrNull()

    fun update(newItems: RecentProducts): RecentProducts = copy(items = newItems.items)

    fun add(newItem: RecentProduct): RecentProducts {
        val newItems = items.toMutableList()
        if (newItems.size == maxCount) newItems.removeLast()
        newItems.add(0, RecentProduct(product = newItem.product))

        return RecentProducts(newItems.take(maxCount), maxCount)
    }

    operator fun plus(newItem: RecentProduct): RecentProducts = add(newItem)
}

package woowacourse.shopping.domain.model.product

class Products(
    val items: List<Product>,
    val isLast: Boolean,
) {
    fun getCategoryProducts(category: String): List<Product> = items.filter { it.category.value == category }

    // TODO: 제거
    fun getPage(
        page: Int,
        pageSize: Int,
    ): List<Product> {
        require(page >= 0) { "page는 0 이상이어야 합니다. page=$page" }
        require(pageSize > 0) { "pageSize는 1 이상이어야 합니다. pageSize=$pageSize" }

        val fromIndex = page * pageSize
        if (fromIndex >= items.size) return emptyList()

        val toIndex = minOf(fromIndex + pageSize, items.size)
        return items.subList(fromIndex, toIndex)
    }
}

package woowacourse.shopping.domain

class RecentProducts(val values: List<RecentProduct> = listOf()) {

    fun getLatestProduct(product: Product): Product? {
        if (values.firstOrNull()?.product == product) {
            return values.secondOrNull()?.product
        }
        return values.firstOrNull()?.product
    }

    private fun List<RecentProduct>.secondOrNull(predicate: ((RecentProduct) -> Boolean)? = null): RecentProduct? {
        var count = 0
        for (element in this) {
            if (predicate == null || predicate(element)) {
                if (count == 1) {
                    return element
                }
                count++
            }
        }
        return null
    }
}

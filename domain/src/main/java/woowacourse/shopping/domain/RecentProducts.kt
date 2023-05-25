package woowacourse.shopping.domain

data class RecentProducts(val value: List<RecentProduct> = emptyList()) {
    fun getRecentProducts(size: Int): RecentProducts {
        return RecentProducts(value.take(size).sortedByDescending { it.time })
    }
}

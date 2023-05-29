package woowacourse.shopping.data.recentproduct

import woowacourse.shopping.model.Product

interface RecentProductRepository {
    fun addRecentProduct(product: Product, callback: () -> Unit)
    fun getRecentProductsBySize(size: Int, callback: (List<Product>) -> Unit)
}

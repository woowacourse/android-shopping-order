package woowacourse.shopping.data.recentproduct

import woowacourse.shopping.data.shoppingpref.ShoppingOrderSharedPreference
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product

class DefaultRecentProductRepository(
    private val recentProductDao: RecentProductDao,
    shoppingOrderPref: ShoppingOrderSharedPreference,
) : RecentProductRepository {

    private val userInfo = shoppingOrderPref.userInfo
    private val baseUrl = shoppingOrderPref.baseUrl
    override fun addRecentProduct(
        product: Product,
        callback: () -> Unit,
    ) {
        val recentProduct = RecentProduct(
            productId = product.id,
            userInfo = userInfo,
            serverUrl = baseUrl,
            imageUrl = product.imageUrl,
            name = product.name,
            price = product.price.value,
            System.currentTimeMillis(),
        )
        Thread {
            requestUpsertRecentProduct(recentProduct, callback)
        }.start()
    }

    private fun requestUpsertRecentProduct(recentProduct: RecentProduct, callback: () -> Unit) {
        recentProductDao.upsertRecentProduct(recentProduct)
        callback()
    }

    override fun getRecentProductsBySize(size: Int, callback: (List<Product>) -> Unit) {
        Thread {
            requestRecentProduct(size, callback)
        }.start()
    }

    private fun requestRecentProduct(size: Int, callback: (List<Product>) -> Unit) {
        val recentProducts = recentProductDao.loadAllRecentProducts(userInfo, baseUrl)
        if (recentProducts.size > size) {
            callback(recentProducts.subList(0, size).toProduct())
        }
        callback(recentProducts.toProduct())
    }

    private fun List<RecentProduct>.toProduct() =
        this.map { Product(it.productId, it.imageUrl, it.name, Price(it.price)) }
}

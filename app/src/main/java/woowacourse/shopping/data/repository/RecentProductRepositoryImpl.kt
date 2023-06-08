package woowacourse.shopping.data.repository

import woowacourse.shopping.Product
import woowacourse.shopping.data.local.recentproduct.RecentProductLocalDataSource
import woowacourse.shopping.data.remote.product.ProductDataSource
import woowacourse.shopping.repository.RecentProductRepository

class RecentProductRepositoryImpl constructor(
    private val recentProductLocalDataSource: RecentProductLocalDataSource,
    productDataSource: ProductDataSource,
) : RecentProductRepository {
    private val productRepository = ProductRepositoryImpl(productDataSource)
    override fun addRecentProductId(recentProductId: Int) {
        recentProductLocalDataSource.addRecentProduct(recentProductId)
    }

    override fun deleteRecentProductId(recentProductId: Int) {
        recentProductLocalDataSource.deleteRecentProduct(recentProductId)
    }

    override fun deleteAllProducts() {
        recentProductLocalDataSource.deleteAllProduct()
    }

    override fun getRecentProducts(size: Int, onSuccess: (List<Product>) -> Unit) {
        val recentProductIdList = recentProductLocalDataSource.getRecentProductIdList(size)
        val productList = mutableListOf<Product>()
        getProducts(0, productList, recentProductIdList, onSuccess)
    }

    private fun getProducts(
        index: Int,
        productList: MutableList<Product>,
        recentProductIdList: List<Int>,
        onSuccess: (List<Product>) -> Unit,
    ) {
        if (recentProductIdList.isEmpty()) return
        productRepository.findProductById(id = recentProductIdList[index], onSuccess = {
            if (it != null) productList.add(it)
            if (index == recentProductIdList.lastIndex) onSuccess(productList)
            if (index + 1 <= recentProductIdList.lastIndex) {
                getProducts(
                    index + 1,
                    productList,
                    recentProductIdList,
                    onSuccess,
                )
            }
        }, onFailure = {})
    }

    override fun getMostRecentProduct(onSuccess: (Product?) -> Unit) {
        val mostRecentProductId = recentProductLocalDataSource.getMostRecentProductId()
        return productRepository.findProductById(
            id = mostRecentProductId,
            onSuccess = { onSuccess(it) },
            onFailure = {}
        )
    }
}

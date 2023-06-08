package woowacourse.shopping.data.datasource.product.remote

import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.httpclient.RetrofitModule
import woowacourse.shopping.data.httpclient.getRetrofitCallback
import woowacourse.shopping.data.model.DataProduct
import java.lang.Integer.min

class RemoteProductDataSource() : ProductDataSource.Remote {

    override fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<DataProduct>) -> Unit
    ) {
        RetrofitModule.productService.getAllProducts().enqueue(
            getRetrofitCallback<List<DataProduct>>(
                failureLogTag = this::class.java.name,
                onResponse = { _, response ->
                    val productCache: List<DataProduct> = response.body() ?: listOf()
                    onReceived(getDataProductsFromCache(size, lastId, productCache))
                }
            )
        )
    }

    private fun getDataProductsFromCache(
        size: Int,
        lastId: Int,
        allProducts: List<DataProduct>
    ): List<DataProduct> {
        if (lastId == -1) return allProducts.subList(0, min(allProducts.size, size))
        val startIndex = allProducts.indexOfFirst { it.id == lastId } + 1
        return allProducts.subList(startIndex, min(allProducts.size, startIndex + size))
    }
}

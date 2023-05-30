package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.NetworkModule
import woowacourse.shopping.data.model.DataProduct
import java.lang.Integer.min

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    private val productService: ProductService = NetworkModule.getService()

    override fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<DataProduct>) -> Unit,
    ) {
        productService.requestProducts().enqueue(object : retrofit2.Callback<List<DataProduct>> {

            override fun onResponse(
                call: retrofit2.Call<List<DataProduct>>,
                response: retrofit2.Response<List<DataProduct>>,
            ) {
                response.body()?.let {
                    onReceived(
                        getDataProductsFromCache(
                            size = size,
                            lastId = lastId,
                            allProducts = it
                        )
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<List<DataProduct>>, t: Throwable) {
            }
        })
    }

    private fun getDataProductsFromCache(
        size: Int,
        lastId: Int,
        allProducts: List<DataProduct>,
    ): List<DataProduct> {
        if (lastId == -1) return allProducts.subList(0, min(allProducts.size, size))
        val startIndex = allProducts.indexOfFirst { it.id == lastId } + 1
        return allProducts.subList(startIndex, min(allProducts.size, startIndex + size))
    }
}

package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.NetworkModule.productService
import woowacourse.shopping.data.datasource.response.ProductEntity
import java.lang.Integer.min

class ProductRemoteDataSourceImpl : ProductRemoteDataSource {

    override fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<ProductEntity>) -> Unit,
    ) {
        productService.requestProducts().enqueue(object : retrofit2.Callback<List<ProductEntity>> {

            override fun onResponse(
                call: retrofit2.Call<List<ProductEntity>>,
                response: retrofit2.Response<List<ProductEntity>>,
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

            override fun onFailure(call: retrofit2.Call<List<ProductEntity>>, t: Throwable) {
            }
        })
    }

    private fun getDataProductsFromCache(
        size: Int,
        lastId: Int,
        allProducts: List<ProductEntity>,
    ): List<ProductEntity> {
        if (lastId == -1) return allProducts.subList(0, min(allProducts.size, size))
        val startIndex = allProducts.indexOfFirst { it.id == lastId } + 1
        return allProducts.subList(startIndex, min(allProducts.size, startIndex + size))
    }
}

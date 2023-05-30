package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.datasource.retrofit.ServicePool
import woowacourse.shopping.data.remote.request.ProductDTO

class ProductDataSourceImpl : ProductDataSource {

    override fun getAllProducts(): retrofit2.Call<List<ProductDTO>> {
        return ServicePool.productDataService.getProducts()
    }
}

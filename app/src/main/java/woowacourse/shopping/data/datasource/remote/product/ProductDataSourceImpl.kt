package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.model.Product
import woowacourse.shopping.data.datasource.retrofit.RetrofitClient
import woowacourse.shopping.data.datasource.retrofit.ServicePool

class ProductDataSourceImpl : ProductDataSource {

    override fun getAllProducts(): retrofit2.Call<List<Product>> {
        return ServicePool.productDataService.getProducts()
    }
}

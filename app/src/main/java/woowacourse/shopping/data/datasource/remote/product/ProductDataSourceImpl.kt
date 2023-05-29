package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.model.Product
import woowacourse.shopping.data.datasource.retrofit.RetrofitClient

class ProductDataSourceImpl : ProductDataSource {

    override fun getAllProducts(): retrofit2.Call<List<Product>> {
        return RetrofitClient.api.getProducts()
    }
}

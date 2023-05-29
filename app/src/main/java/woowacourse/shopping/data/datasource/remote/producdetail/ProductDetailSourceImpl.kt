package woowacourse.shopping.data.datasource.remote.producdetail

import com.example.domain.model.Product
import retrofit2.Call
import woowacourse.shopping.data.datasource.retrofit.RetrofitClient

class ProductDetailSourceImpl : ProductDetailSource {
    override fun getById(id: Long): Call<Product> {
        return RetrofitClient.api.getProductById(id)
    }
}

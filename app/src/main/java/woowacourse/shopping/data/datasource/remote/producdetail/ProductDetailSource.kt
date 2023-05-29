package woowacourse.shopping.data.datasource.remote.producdetail

import com.example.domain.model.Product
import retrofit2.Call

interface ProductDetailSource {
    fun getById(id: Long): Call<Product>
}

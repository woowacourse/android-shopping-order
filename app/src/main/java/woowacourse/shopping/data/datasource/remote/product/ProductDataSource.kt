package woowacourse.shopping.data.datasource.remote.product

import com.example.domain.model.Product
import retrofit2.Call

interface ProductDataSource {

    fun getAllProducts(): Call<List<Product>>
}

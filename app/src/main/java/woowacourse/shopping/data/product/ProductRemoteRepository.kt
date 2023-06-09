package woowacourse.shopping.data.product

import com.example.domain.Pagination
import com.example.domain.product.Product
import retrofit2.Retrofit
import retrofit2.create
import woowacourse.shopping.data.cart.model.toDomain
import woowacourse.shopping.data.product.model.dto.ProductDto
import woowacourse.shopping.data.product.model.dto.response.ProductsResponse
import woowacourse.shopping.data.product.model.toDomain
import woowacourse.shopping.data.util.RetrofitCallback

class ProductRemoteRepository(
    retrofit: Retrofit
) : ProductRepository {

    private val retrofitProductService: RetrofitProductService = retrofit.create()

    override fun requestFetchProductsUnit(
        unitSize: Int,
        page: Int,
        success: (List<Product>, Pagination) -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : RetrofitCallback<ProductsResponse>() {
            override fun onSuccess(response: ProductsResponse?) {
                if (response == null) return
                val products: List<Product> = response.products.map(ProductDto::toDomain)
                val pagination: Pagination = response.pagination.toDomain()
                success(products, pagination)
            }
        }
        retrofitProductService
            .requestFetchProductsUnit(unitSize = unitSize, page = page)
            .enqueue(retrofitCallback)
    }

    override fun requestFetchProductById(
        id: Long,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    ) {
        val retrofitCallback = object : RetrofitCallback<Product>() {
            override fun onSuccess(response: Product?) {
                if (response == null) return
                onSuccess(response)
            }
        }
        retrofitProductService.requestFetchProductById(id).enqueue(retrofitCallback)
    }
}

package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.ProductGetResponse
import woowacourse.shopping.data.dto.mapper.toDomain
import woowacourse.shopping.data.dto.mapper.toProductDeleteRequest
import woowacourse.shopping.data.dto.mapper.toProductPostRequest
import woowacourse.shopping.data.dto.mapper.toProductPutRequest
import woowacourse.shopping.data.service.cart.ProductId
import woowacourse.shopping.data.service.product.ProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productService: ProductService,
) : ProductRepository {

    override fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        productService.getAllProduct().enqueue(object : Callback<List<ProductGetResponse>> {
            override fun onResponse(
                call: Call<List<ProductGetResponse>>,
                response: Response<List<ProductGetResponse>>,
            ) {
                onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
            }

            override fun onFailure(call: Call<List<ProductGetResponse>>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }

    override fun findProductById(
        id: ProductId,
        onSuccess: (Product?) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        productService.findProductById(id).enqueue(object : Callback<ProductGetResponse?> {
            override fun onResponse(
                call: Call<ProductGetResponse?>,
                response: Response<ProductGetResponse?>,
            ) {
                onSuccess(response.body()?.toDomain())
            }

            override fun onFailure(call: Call<ProductGetResponse?>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }

    override fun saveProduct(product: Product) {
        productService.saveProduct(product.toProductPostRequest())
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                override fun onFailure(call: Call<Unit>, throwable: Throwable) {}
            })
    }

    override fun updateProduct(product: Product) {
        productService.updateProduct(product.id, product.toProductPutRequest())
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                override fun onFailure(call: Call<Unit>, throwable: Throwable) {}
            })
    }

    override fun deleteProduct(product: Product) {
        productService.deleteProduct(product.id, product.toProductDeleteRequest())
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                override fun onFailure(call: Call<Unit>, throwable: Throwable) {}
            })
    }
}

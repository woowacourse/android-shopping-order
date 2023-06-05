package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.ProductItemResponse
import woowacourse.shopping.data.dto.ProductsResponse
import woowacourse.shopping.data.dto.mapper.toDomain
import woowacourse.shopping.data.dto.mapper.toProductDeleteRequest
import woowacourse.shopping.data.dto.mapper.toProductPostRequest
import woowacourse.shopping.data.dto.mapper.toProductPutRequest
import woowacourse.shopping.data.service.cart.ProductId
import woowacourse.shopping.data.service.product.ProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val productService: ProductService,
) : ProductRepository {

    override fun getProductsByPage(
        page: Page,
        onSuccess: (List<Product>) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        productService.getProductByPage(page.value, page.sizePerPage).enqueue(object : Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>,
            ) {
                if (response.body() != null && response.isSuccessful) {
                    onSuccess(response.body()?.products?.map { it.toDomain() } ?: emptyList())
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<ProductsResponse>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }

    override fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        productService.getAllProduct().enqueue(object : Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>,
            ) {
                if (response.body() != null && response.isSuccessful) {
                    onSuccess(response.body()?.products?.map { it.toDomain() } ?: emptyList())
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<ProductsResponse>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }

    override fun findProductById(
        id: ProductId,
        onSuccess: (Product?) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        productService.findProductById(id).enqueue(object : Callback<ProductItemResponse?> {
            override fun onResponse(
                call: Call<ProductItemResponse?>,
                response: Response<ProductItemResponse?>,
            ) {
                if (response.body() != null && response.isSuccessful) {
                    onSuccess(response.body()?.toDomain())
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<ProductItemResponse?>, throwable: Throwable) {
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

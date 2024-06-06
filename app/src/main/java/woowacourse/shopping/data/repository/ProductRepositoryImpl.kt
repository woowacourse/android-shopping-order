package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ApiHandleCartDataSource
import woowacourse.shopping.data.datasource.ApiHandleProductDataSource
import woowacourse.shopping.data.datasource.impl.ApiHandleCartDataSourceImpl
import woowacourse.shopping.data.datasource.impl.ApiHandleProductDataSourceImpl
import woowacourse.shopping.data.local.room.cart.Cart
import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDataSource: ApiHandleProductDataSource = ApiHandleProductDataSourceImpl(),
    private val cartDataSource: ApiHandleCartDataSource = ApiHandleCartDataSourceImpl()
) :
    ProductRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): ApiResponse<List<Product>> = handleResponse(
        productDataSource.getProductsByOffset(page, size),
        transform = ::toProductList
    )


    override suspend fun find(id: Long): ApiResponse<Product> =
        handleResponse(productDataSource.getProductsById(id), transform = ::toProduct)

    override suspend fun productsByCategory(category: String): ApiResponse<List<Product>> {

        val count: Int = when (val countResponse = cartDataSource.getCartItemCounts()) {
            is ApiResult.Success -> countResponse.data.quantity
            is ApiResult.Error -> return handleError(countResponse)
            is ApiResult.Exception -> return ApiResponse.Exception(countResponse.e)
        }


        val carts: List<Cart> = when (val cartResponse = cartDataSource.getCartItems(0, count)) {
            is ApiResult.Success -> cartResponse.data.content.map {
                Cart(
                    it.id,
                    it.product.id,
                    Quantity(it.quantity)
                )
            }

            is ApiResult.Error -> return handleError(cartResponse)
            is ApiResult.Exception -> return ApiResponse.Exception(cartResponse.e)
        }


        var page = 0
        var products = mutableListOf<Product>()
        var loadedProducts = listOf<Product>()
        while (true) {
            when (val productResponse = productDataSource.getProductsByOffset(page, 20)) {
                is ApiResult.Success -> loadedProducts = toProductList(productResponse.data)
                is ApiResult.Error -> return handleError(productResponse)
                is ApiResult.Exception -> return ApiResponse.Exception(productResponse.e)
            }

            if (loadedProducts.isEmpty() || products.size >= 10) break
            products.addAll(loadedProducts.filter { it.category == category }
                .filterNot { carts.map { it.productId }.contains(it.id) })
            page++
        }
        return ApiResponse.Success<List<Product>>(products)
    }

    private fun toProductList(dto: ResponseProductsGetDto): List<Product> {
        val productDto = dto.content
        return productDto.map {
            Product(it.id, it.imageUrl, it.name, it.price, it.category)
        }
    }

    private fun toProduct(dto: ResponseProductIdGetDto): Product = Product(
        dto.id, dto.imageUrl, dto.name, dto.price, dto.category
    )
}


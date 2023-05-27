package woowacourse.shopping.data.repository.remote

import com.example.domain.cache.ProductCache
import com.example.domain.cache.ProductLocalCache
import com.example.domain.model.Price
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.service.ProductService
import woowacourse.shopping.data.model.dto.request.product.ProductDto

class ProductRepositoryImpl(
    private val service: ProductService,
    override val cache: ProductCache = ProductLocalCache,
) : ProductRepository {
    private val allProducts: MutableList<Product> = mutableListOf()
    override fun fetchFirstProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        if (cache.productList.isEmpty()) {
            service.getAllProducts().enqueue(object : Callback<List<ProductDto>> {
                override fun onResponse(
                    call: Call<List<ProductDto>>,
                    response: Response<List<ProductDto>>
                ) {
                    val products = response.body() ?: emptyList()
                    allProducts.clear()
                    allProducts.addAll(
                        products.map {
                            with(it) {
                                Product(id.toLong(), name, imageUrl, Price(price))
                            }
                        }
                    )
                    val nextProducts = allProducts.take(UNIT_SIZE)
                    cache.addProducts(nextProducts)
                    onSuccess(nextProducts)
                }

                override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                    onFailure()
                }
            })
        } else {
            onSuccess(cache.productList)
        }
    }

    override fun fetchNextProducts(
        lastProductId: Long,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        val nextProducts = allProducts.filter { it.id > lastProductId }.take(UNIT_SIZE)
        if (nextProducts.isEmpty()) onFailure()
        cache.addProducts(nextProducts)
        onSuccess(nextProducts)
    }

    override fun fetchProductById(
        productId: Long,
        onSuccess: (Product) -> Unit,
        onFailure: () -> Unit,
    ) {
        service.getProductById(productId).enqueue(object : Callback<ProductDto> {
            override fun onResponse(call: Call<ProductDto>, response: Response<ProductDto>) {
                val product = response.body() ?: return onFailure()
                onSuccess(
                    with(product) {
                        Product(id.toLong(), name, imageUrl, Price(price))
                    }
                )
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun resetCache() {
        cache.clear()
    }

    companion object {
        private const val UNIT_SIZE = 20
    }
}

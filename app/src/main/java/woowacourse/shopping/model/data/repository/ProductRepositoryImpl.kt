package woowacourse.shopping.model.data.repository

import com.shopping.domain.Product
import com.shopping.repository.ProductRepository
import woowacourse.shopping.model.data.dto.ProductDTO
import woowacourse.shopping.server.retrofit.ProductsService
import woowacourse.shopping.server.retrofit.createResponseCallback

class ProductRepositoryImpl(
    private val service: ProductsService
) : ProductRepository {

    override fun loadProducts(
        index: Pair<Int, Int>,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        service.getAllProducts().enqueue(
            createResponseCallback(
                onSuccess = { products ->
                    if (index.first >= products.size) {
                        onSuccess(emptyList())
                    }
                    onSuccess(
                        products.map { it.toDomain() }.subList(index.first, minOf(index.second, products.size))
                    )
                },
                onFailure = onFailure
            )
        )
    }

    override fun getProductById(
        index: Int,
        onSuccess: (Product) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        service.getProduct(index.toLong()).enqueue(
            createResponseCallback(
                onSuccess = { product ->
                    onSuccess(product.toDomain())
                },
                onFailure = onFailure
            )
        )
    }

    private fun ProductDTO.toDomain(): Product =
        Product(id, name, imageUrl, price)
}

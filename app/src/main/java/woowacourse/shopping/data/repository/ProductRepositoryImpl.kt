package woowacourse.shopping.data.repository

import com.example.domain.model.CartProduct
import com.example.domain.model.Product
import com.example.domain.model.ProductItem
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.data.datasource.remote.product.ProductRemoteDataSource
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSource
import woowacourse.shopping.mapper.toDomain

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val cartRemoteDataSource: ShoppingCartDataSource,
) : ProductRepository {
    override fun getMoreProducts(
        limit: Int,
        scrollCount: Int,
        callback: (List<ProductItem>) -> Unit,
    ) {
        productRemoteDataSource.getSubListProducts(limit, scrollCount) { it ->
            if (it.isSuccess) {
                val products = it.getOrNull()?.map { product -> product.toDomain() } ?: throw IllegalArgumentException()
                insertProductCount(products, callback)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    private fun insertProductCount(
        products: List<Product>,
        callback: (List<ProductItem>) -> Unit,
    ) {
        cartRemoteDataSource.getAllProductInCart {
            if (it.isSuccess) {
                val cartProducts = it.getOrNull()?.map { cartProduct -> cartProduct.toDomain() } ?: throw IllegalArgumentException()
                val productItems = joinProductQuantity(products, cartProducts)
                callback(productItems)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    private fun joinProductQuantity(products: List<Product>, cartProducts: List<CartProduct>): List<ProductItem> {
        return products.map { product ->
            val cartProduct = cartProducts.find { it.product.id == product.id }
            val quantity = cartProduct?.quantity ?: 0
            ProductItem(product, quantity)
        }
    }
}

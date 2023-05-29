package woowacourse.shopping.ui.shopping.contract.presenter

import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.utils.OffsetInterface

class ProductsOffset(offset: Int, private val repositoryImpl: ProductRepository) :
    OffsetInterface {
    private var offset: Int = offset
        set(value) {
            val productList = mutableListOf<Product>()
            repositoryImpl.getAllProducts(
                onSuccess = { products ->
                    productList.addAll(products)
                    field = when {
                        value < 0 -> 0
                        value > productList.size -> productList.size
                        else -> value
                    }
                },
                onFailure = {},
            )
        }

    override fun plus(value: Int): ProductsOffset = ProductsOffset(offset + value, repositoryImpl)

    override fun minus(value: Int): ProductsOffset = ProductsOffset(offset - value, repositoryImpl)

    override fun getOffset(): Int = offset
}

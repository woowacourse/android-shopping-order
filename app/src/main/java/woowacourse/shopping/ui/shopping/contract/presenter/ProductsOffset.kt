package woowacourse.shopping.ui.shopping.contract.presenter

import com.example.domain.repository.ProductRepository
import woowacourse.shopping.utils.OffsetInterface

class ProductsOffset(offset: Int, private val repositoryImpl: ProductRepository) :
    OffsetInterface {
    private var offset: Int = offset

    override fun plus(value: Int): ProductsOffset = ProductsOffset(offset + value, repositoryImpl)

    override fun minus(value: Int): ProductsOffset = ProductsOffset(offset - value, repositoryImpl)

    override fun getOffset(): Int = offset
}

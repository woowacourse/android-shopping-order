package woowacourse.shopping.feature

import com.example.domain.datasource.productsDatasource
import com.example.domain.model.CartProduct

internal object CartFixture {
    fun getMockCarts(): List<CartProduct> {
        return listOf(
            CartProduct(1L, mockProducts[0], 3, true),
            CartProduct(2L, mockProducts[1], 2, true),
            CartProduct(3L, mockProducts[2], 1, true),
            CartProduct(4L, mockProducts[3], 2, true),
        )
    }

    fun getMockCartItems(ids: List<Long>): List<CartProduct> {
        return getMockCarts().filter { it.cartId in ids }
    }

    private val mockProducts = productsDatasource
}

package woowacourse.shopping.feature

import com.example.domain.datasource.productsDatasource
import com.example.domain.model.CartProduct

internal object CartFixture {
    fun getMockCarts(): List<CartProduct> {
        return listOf(
            CartProduct(1L, mockProducts[0], 3, true), // 2000원
            CartProduct(2L, mockProducts[1], 2, true), // 13000원
            CartProduct(3L, mockProducts[2], 1, true), // 9000원
            CartProduct(4L, mockProducts[3], 2, true), // 4000원
        )
    }

    fun getMockCartItems(ids: List<Long>): List<CartProduct> {
        return getMockCarts().filter { it.cartId in ids }
    }

    private val mockProducts = productsDatasource
}

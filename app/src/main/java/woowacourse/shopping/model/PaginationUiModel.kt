package woowacourse.shopping.model

import com.example.domain.model.CartProducts
import com.example.domain.model.Pagination
import woowacourse.shopping.mapper.toDomain

data class PaginationUiModel(
    private val cartProducts: List<CartProductUiModel>,
    val currentPageCartProducts: List<CartProductUiModel>,
    val pageBottomNavigationUiModel: PageBottomNavigationUiModel,
    val cartBottomNavigationUiModel: CartBottomNavigationUiModel,
    val pageTotalCount: Int,
) {
    val checkedCartIds: List<Long>
        get() = cartProducts.filter { it.checked }.map { it.cartId }

    val selectedCount: Int
        get() = cartProducts.filter { it.checked }.sumOf { it.productUiModel.count }

    fun toDomain(): Pagination {
        return Pagination(
            CartProducts(cartProducts.map { it.toDomain() }),
            pageBottomNavigationUiModel.currentPageNumber,
        )
    }
}

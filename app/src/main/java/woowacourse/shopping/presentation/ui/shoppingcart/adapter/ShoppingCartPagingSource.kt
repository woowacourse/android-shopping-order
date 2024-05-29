package woowacourse.shopping.presentation.ui.shoppingcart.adapter

import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.ui.shoppingcart.PagingCartProduct

class ShoppingCartPagingSource(private val repository: ShoppingCartRepository) {
    fun load(page: Int): Result<PagingCartProduct> {
        val result = repository.getCartProductsPaged(page = page, size = PAGING_SIZE)

        return result.fold(
            onSuccess = { cardProducts ->
                Result.success(PagingCartProduct(cardProducts.content, cardProducts.pageable.pageNumber, cardProducts.last))
            },
            onFailure = { e ->
                Result.failure(e)
            },
        )
    }

    companion object {
        private const val PAGING_SIZE = 5
    }
}

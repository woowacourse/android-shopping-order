package woowacourse.shopping.presentation.ui.productlist.adapter

import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.ui.productlist.PagingProduct

class ProductListPagingSource(private val productRepository: ProductRepository) {
    private var currentPage = INIT_PAGE_NUM
    private var last = false

    suspend fun load(): Result<PagingProduct> {
        if (last) return Result.failure(NoSuchElementException())

        val result = productRepository.getPagingProduct(page = currentPage, pageSize = PAGING_SIZE)

        return result.fold(
            onSuccess = { products ->
                if (products.content.size < PAGING_SIZE) last = true
                currentPage++
                Result.success(PagingProduct(products.content, last))
            },
            onFailure = { e ->
                Result.failure(e)
            },
        )
    }

    companion object {
        private const val PAGING_SIZE = 20
        private const val INIT_PAGE_NUM = 0
    }
}

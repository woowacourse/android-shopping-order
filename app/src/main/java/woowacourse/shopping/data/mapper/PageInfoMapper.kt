package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.domain.model.PageInfo

fun ProductResponseDto.extractPageInfo(): PageInfo {
    return PageInfo(
        isPageable = this.totalPages - 1 > this.pageable.pageNumber,
        currentPage = this.pageable.pageNumber,
        totalPages = this.totalPages,
    )
}

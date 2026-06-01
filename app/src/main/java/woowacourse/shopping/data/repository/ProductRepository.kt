package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Page
import woowacourse.shopping.model.product.Product

interface ProductRepository {
    /**
     * 페이지 단위로 상품 목록을 조회합니다.
     *
     * @param page 조회할 페이지 인덱스 (0-based)
     * @param size 한 페이지당 상품 개수
     * @return 해당 페이지의 상품 목록과 페이지네이션 메타데이터
     */
    suspend fun getProducts(
        page: Int,
        size: Int,
    ): Page<Product>

    suspend fun findProduct(id: Long): Product?
}

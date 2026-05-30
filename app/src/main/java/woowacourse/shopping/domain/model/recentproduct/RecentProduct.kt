package woowacourse.shopping.domain.model.recentproduct

data class RecentProduct(
    val productId: Long,
    val viewedAtMillis: Long,
) {
    init {
        require(viewedAtMillis >= 0) { "최근 본 상품 조회 시각은 0 이상이어야 합니다." }
    }
}

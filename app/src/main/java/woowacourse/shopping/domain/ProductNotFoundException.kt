package woowacourse.shopping.domain

class ProductNotFoundException(
    productId: Long,
) : RuntimeException("존재하지 않는 상품입니다. (id=$productId)")

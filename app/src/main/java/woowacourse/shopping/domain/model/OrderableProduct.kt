package woowacourse.shopping.domain.model

data class OrderableProduct(
    val productItemDomain: ProductItemDomain,
    val cartData: CartData?,
)

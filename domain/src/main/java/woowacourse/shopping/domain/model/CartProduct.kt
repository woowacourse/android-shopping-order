package woowacourse.shopping.domain.model

data class CartProduct(val cartId: Int, val count: Int, val product: Product) {

    companion object
}

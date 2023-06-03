package woowacourse.shopping.domain.model

data class OrderDTO(val orderId: Int, val orderedDateTime: String, val products: List<ProductWithQuantity>, val totalPrice: Int) {
    data class ProductWithQuantity(val product: Product, val quantity: Int)
}

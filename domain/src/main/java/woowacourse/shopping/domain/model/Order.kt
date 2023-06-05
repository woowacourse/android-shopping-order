package woowacourse.shopping.domain.model

data class Order(val orderId: Int, val orderedDateTime: String, val products: List<ProductWithQuantity>, val totalPrice: Int)

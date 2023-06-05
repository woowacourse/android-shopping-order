package woowacourse.shopping.data.remote.dto

import woowacourse.shopping.domain.model.ProductWithQuantity

data class OrderDTO(val orderId: Int, val orderedDateTime: String, val products: List<ProductWithQuantity>, val totalPrice: Int)

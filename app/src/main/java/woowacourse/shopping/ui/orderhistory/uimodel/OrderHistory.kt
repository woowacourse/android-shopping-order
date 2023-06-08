package woowacourse.shopping.ui.orderhistory.uimodel

import com.example.domain.model.OrderProduct

data class OrderHistory(
    val id: Int,
    val orderProducts: List<OrderProduct>,
    val originPrice: Int,
    val couponName: String?,
    val totalPrice: Int,
) {
    fun getTotalCount() = orderProducts.size
    fun getProductsName(): String {
        val nameContainer = orderProducts.map { it.product.name }

        return nameContainer.joinToString(",", "", "")
    }
}

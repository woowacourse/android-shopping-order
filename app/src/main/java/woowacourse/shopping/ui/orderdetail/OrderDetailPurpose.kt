package woowacourse.shopping.ui.orderdetail

enum class OrderDetailPurpose {
    SHOW_ORDER_COMPLETE, SHOW_ORDER_DETAIL;

    companion object {
        fun getPurpose(purpose: String) : OrderDetailPurpose = values().find { it.name == purpose } ?: SHOW_ORDER_COMPLETE
    }
}
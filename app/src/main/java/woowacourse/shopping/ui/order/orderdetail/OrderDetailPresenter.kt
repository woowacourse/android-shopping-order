package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.ui.order.uistate.DiscountPolicyUIState
import woowacourse.shopping.ui.order.uistate.OrderUIState
import woowacourse.shopping.ui.productlist.uistate.ProductUIState

class OrderDetailPresenter(private val view: OrderDetailContract.View) :
    OrderDetailContract.Presenter {
    override fun loadOrder() {
        val order = OrderUIState(
            0, 0,
            listOf(
                ProductUIState(
                    0,
                    "https://i.namu.wiki/i/NnyulKv-E_tHg-hCunm5JxbhUIE-NaweaFHC2L-NtB4paM4wFNFDTqHKP1w0Tjs5O8n--TI21S8sxNQsHlM59ImjygLJqDscWqy6Ao0nMGVQM2RjCXelArwjXrwVkOUffnnt-X3xSxKBQOtixulhgQ.webp",
                    "0",
                    0,
                    0
                ),
                ProductUIState(
                    1,
                    "https://i.namu.wiki/i/NnyulKv-E_tHg-hCunm5JxbhUIE-NaweaFHC2L-NtB4paM4wFNFDTqHKP1w0Tjs5O8n--TI21S8sxNQsHlM59ImjygLJqDscWqy6Ao0nMGVQM2RjCXelArwjXrwVkOUffnnt-X3xSxKBQOtixulhgQ.webp",
                    "1",
                    0,
                    0
                ),
                ProductUIState(
                    2,
                    "https://i.namu.wiki/i/NnyulKv-E_tHg-hCunm5JxbhUIE-NaweaFHC2L-NtB4paM4wFNFDTqHKP1w0Tjs5O8n--TI21S8sxNQsHlM59ImjygLJqDscWqy6Ao0nMGVQM2RjCXelArwjXrwVkOUffnnt-X3xSxKBQOtixulhgQ.webp",
                    "2",
                    0,
                    0
                )
            ),
            listOf(
                DiscountPolicyUIState("회원 등급 할인", 5, 10000),
                DiscountPolicyUIState("금액 구간 할인", 5, 10000)
            )
        )

        view.showOrder(order)
    }
}

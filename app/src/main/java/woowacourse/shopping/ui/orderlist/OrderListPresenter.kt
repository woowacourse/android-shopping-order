package woowacourse.shopping.ui.orderlist

import woowacourse.shopping.ui.orderlist.uistate.OrderUIState
import woowacourse.shopping.ui.productlist.uistate.ProductUIState

class OrderListPresenter(private val view: OrderListContract.View) : OrderListContract.Presenter {
    override fun loadOrders() {
        val orders = listOf(
            OrderUIState(
                0,
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
                )
            ),
            OrderUIState(
                1,
                listOf(
                    ProductUIState(
                        0,
                        "https://i.namu.wiki/i/NnyulKv-E_tHg-hCunm5JxbhUIE-NaweaFHC2L-NtB4paM4wFNFDTqHKP1w0Tjs5O8n--TI21S8sxNQsHlM59ImjygLJqDscWqy6Ao0nMGVQM2RjCXelArwjXrwVkOUffnnt-X3xSxKBQOtixulhgQ.webp",
                        "2",
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
                )
            )
        )
        view.showOrders(orders)
    }
}

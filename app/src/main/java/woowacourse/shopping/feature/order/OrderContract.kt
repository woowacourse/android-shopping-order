package woowacourse.shopping.feature.order

import com.example.domain.Cart

interface OrderContract {
    interface View {
        fun setOrderProducts(orderProducts: Cart)
        fun setProductsSum(productsSum: Int)
    }

    interface Presenter {
        fun loadOrderProducts()
        fun calculatePrice()
    }
}

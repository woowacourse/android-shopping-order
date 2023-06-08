package woowacourse.shopping.error

import woowacourse.shopping.R

sealed class DataError(override val message: String?) : Throwable() {
    abstract val viewMessage: Int

    class UserError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_user
    }
    class CartSaveError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_cart_save
    }
    class CartFindError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_cart_find
    }
    class CartUpdateError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_cart_update
    }
    class CartDeleteError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_cart_delete
    }
    class OrderSaveError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_order_save
    }
    class OrderFindError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_order_find
    }
    class ProductFindError(message: String?) : DataError(message) {
        override val viewMessage: Int = R.string.error_cart_find
    }
}

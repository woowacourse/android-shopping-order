package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.R

enum class OrderMessageEvent {
    FETCH_CART_ITEMS_FAILURE,
    DELETE_CART_ITEM_FAILURE,
    PATCH_CART_PRODUCT_QUANTITY_FAILURE,
    FIND_PRODUCT_QUANTITY_FAILURE,
    FETCH_SUGGESTION_PRODUCT_FAILURE,
    ORDER_CART_ITEMS_SUCCESS,
    ORDER_CART_ITEMS_FAILURE,
}

fun OrderMessageEvent.toMessageResId(): Int =
    when (this) {
        OrderMessageEvent.FETCH_CART_ITEMS_FAILURE -> R.string.cart_screen_event_message_fetch_cart_items_failure

        OrderMessageEvent.DELETE_CART_ITEM_FAILURE ->
            R.string.cart_screen_event_message_delete_cart_item_failure

        OrderMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE ->
            R.string.cart_screen_event_message_patch_cart_product_quantity_failure

        OrderMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE ->
            R.string.cart_screen_event_message_find_quantity_failure

        OrderMessageEvent.FETCH_SUGGESTION_PRODUCT_FAILURE ->
            R.string.cart_screen_event_message_fetch_suggestion_product_failure

        OrderMessageEvent.ORDER_CART_ITEMS_FAILURE -> R.string.suggestion_screen_event_message_order_failure
        OrderMessageEvent.ORDER_CART_ITEMS_SUCCESS -> R.string.suggestion_screen_event_message_order_success
    }

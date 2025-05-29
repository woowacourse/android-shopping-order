package woowacourse.shopping.view.common

enum class ResultFrom {
    SHOPPING_CART_BACK,
    PRODUCT_DETAIL_BACK,
    PRODUCT_RECENT_WATCHING_CLICK,
    RECOMMEND_PRODUCT_BACK,
    ;

    val RESULT_OK: Int = ordinal + 1
}

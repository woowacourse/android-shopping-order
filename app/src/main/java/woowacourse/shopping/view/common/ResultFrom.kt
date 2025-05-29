package woowacourse.shopping.view.common

enum class ResultFrom {
    SHOPPING_CART_BACK,
    PRODUCT_DETAIL_BACK,
    PRODUCT_RECENT_WATCHING_CLICK,
    RECOMMEND_PRODUCT_BACK,
    ;

    @Suppress("ktlint:standard:property-naming")
    val RESULT_OK: Int = ordinal + 1
}

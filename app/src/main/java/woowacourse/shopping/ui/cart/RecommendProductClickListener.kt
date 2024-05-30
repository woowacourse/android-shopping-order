package woowacourse.shopping.ui.cart

interface RecommendProductClickListener {
    fun plusRecommendCount(productId: Long)

    fun minusRecommendCount(productId: Long)

}

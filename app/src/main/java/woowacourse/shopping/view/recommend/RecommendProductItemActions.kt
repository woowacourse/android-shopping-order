package woowacourse.shopping.view.recommend

interface RecommendProductItemActions {
    fun onPlusProductQuantity(item: RecommendProduct)

    fun onMinusProductQuantity(item: RecommendProduct)
}

package woowacourse.shopping.view.recommend

interface RecommendProductItemActions {
    fun onSelectProduct(item: RecommendProduct)

    fun onPlusProductQuantity(item: RecommendProduct)

    fun onMinusProductQuantity(item: RecommendProduct)
}

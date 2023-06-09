package woowacourse.shopping.data.discount.source

interface NetworkDiscountInfoDataSource {

    fun loadDiscountInfo(price: Int): Result<NetworkDiscountInfo>
}

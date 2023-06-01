package woowacourse.shopping.domain.repository

interface MypageRepository {
    fun getCash(callback: (Int) -> Unit)
    fun chargeCash(cash: Int, callback: (Int) -> Unit)
}

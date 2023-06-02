package woowacourse.shopping.feature.order

import com.example.domain.model.MerchandiseItem

class Discount(private val originalPrice: Int) : MerchandiseItem {
    override fun use(): Pair<Int, Condition?> {
        Condition.values().forEach {
            if (originalPrice - it.standardPrice >= 0) {
                return Pair(originalPrice - it.amount, it)
            }
        }
        return Pair(originalPrice, null)
    }

    enum class Condition(val standardPrice: Int, val amount: Int) {
        CONDITION_50000(50000, 5000),
        CONDITION_30000(30000, 3000),
    }
}

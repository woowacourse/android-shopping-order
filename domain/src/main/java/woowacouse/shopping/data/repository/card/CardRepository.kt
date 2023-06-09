package woowacouse.shopping.data.repository.card

import woowacouse.shopping.model.card.Card

interface CardRepository {
    fun loadCards(): List<Card>
}

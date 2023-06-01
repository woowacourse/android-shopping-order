package woowacourse.shopping.data.respository.card

import woowacourse.shopping.data.mapper.toModel
import woowacouse.shopping.data.repository.card.CardRepository
import woowacouse.shopping.model.card.Card

class CardRepositoryImpl : CardRepository {
    override fun loadCards(): List<Card> {
        return CardDAO.getCards().map { it.toModel() }
    }
}

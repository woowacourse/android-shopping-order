package woowacourse.shopping.data.respository.card

import woowacourse.shopping.data.mapper.toModel
import woowacouse.shopping.data.repository.card.CardRepository
import woowacouse.shopping.model.card.Card

class CardRepositoryImpl(
    private val cardDAO: CardDAO,
) : CardRepository {
    override fun loadCards(): List<Card> {
        return cardDAO.getCards().map { it.toModel() }
    }
}

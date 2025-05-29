@file:Suppress("ktlint:standard:filename")

package woowacourse.shopping.data.util.mapper

import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.domain.model.Goods

fun Content.toDomain(): Goods = Goods(this.name, this.price, this.imageUrl, this.id, this.category)

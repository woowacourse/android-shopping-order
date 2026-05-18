package woowacourse.shopping.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class Product(
    val category: String,
    val id: Long,
    val imageUri: String,
    val name: String,
    val price: Int,
) {
    init {
        require(category.isNotEmpty()) { "카테고리는 빈 값이 될 수 없습니다." }
        require(imageUri.isNotBlank()) { "imageUri는 빈 값이 될 수 없습니다." }
        require(name.isNotBlank()) { "name은 빈 값이 될 수 없습니다." }
        require(price > 0) { "가격은 0원 초과여야 합니다." }
    }
}

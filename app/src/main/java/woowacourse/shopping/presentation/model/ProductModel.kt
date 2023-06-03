package woowacourse.shopping.presentation.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val id: Long,
    @SerializedName("name")
    val title: String,
    val price: Int,
    val imageUrl: String,
) : Parcelable

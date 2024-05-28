package woowacourse.shopping.remote.dto.response


import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PageConfigResponse(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
)
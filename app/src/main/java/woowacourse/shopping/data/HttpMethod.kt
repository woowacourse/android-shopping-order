package woowacourse.shopping.data

import okhttp3.RequestBody

sealed interface HttpMethod {
    val name: String
    val body: RequestBody?

    object Get : HttpMethod {
        override val name: String = "GET"
        override val body: RequestBody? = null
    }

    class Post(
        override val body: RequestBody,
    ) : HttpMethod {
        override val name: String = "POST"
    }

    class Delete(
        override val body: RequestBody? = null,
    ) : HttpMethod {
        override val name: String = "DELETE"
    }
}

package woowacourse.shopping.data.members

import retrofit2.Call
import retrofit2.http.GET

interface MemberRemoteService {

    @GET("members")
    fun requestMembers(): Call<List<MemberDto>>
}

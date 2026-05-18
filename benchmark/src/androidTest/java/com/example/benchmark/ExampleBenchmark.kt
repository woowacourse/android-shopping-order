package com.example.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@JsonClass(generateAdapter = true)
@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val isActive: Boolean,
    val roles: List<String>,
)

@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val dummyUsers =
        List(1000) { i ->
            User(
                id = i,
                name = "User_$i",
                email = "user_$i@example.com",
                isActive = i % 2 == 0,
                roles = listOf("admin", "user"),
            )
        }

    private val kotlinxJson = Json { encodeDefaults = true }
    private val gson = Gson()
    private val jackson = jacksonObjectMapper()

    private val moshi = Moshi.Builder().build()

    private val moshiAdapter =
        moshi.adapter<List<User>>(
            Types.newParameterizedType(List::class.java, User::class.java),
        )

    @Test
    fun kotlinxSerialization_test() {
        benchmarkRule.measureRepeated {
            kotlinxJson.encodeToString(dummyUsers)
        }
    }

    @Test
    fun jackson_test() {
        benchmarkRule.measureRepeated {
            jackson.writeValueAsString(dummyUsers)
        }
    }

    @Test
    fun moshi_reflection_tets() {
        benchmarkRule.measureRepeated {
            moshiAdapter.toJson(dummyUsers)
        }
    }

    @Test
    fun gson_test() {
        benchmarkRule.measureRepeated {
            gson.toJson(dummyUsers)
        }
    }
}

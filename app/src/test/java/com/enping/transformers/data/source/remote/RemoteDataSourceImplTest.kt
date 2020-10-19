package com.enping.transformers.data.source.remote

import com.enping.transformers.data.model.Team
import com.enping.transformers.data.model.Transformer
import com.google.common.truth.Truth
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.declare


internal class RemoteDataSourceImplTest : KoinTest {
    private val gson = Gson()
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<MockWebServer> { MockWebServer() }
        })
    }

    @Before
    fun setup() {
        val server :MockWebServer = get()
        server.start()
        declare { server.url("/") }
    }

    @Test
    fun `give no AllSpark when request all spark then get it from server`() {
        val allSpark = "token"
        val mockResponse = MockResponse().apply {
            setBody(allSpark)
            setHeader("Content-Type", "text/html; charset=utf-8")
        }
        val remote = RemoteDataSourceImpl(get())
        get<MockWebServer>().enqueue(mockResponse)
        val response = runBlocking {
            remote.createAllSpark()
        }

        val request = get<MockWebServer>().takeRequest()
        Truth.assertThat(request.path).isEqualTo("/allspark")
        Truth.assertThat(response).isEqualTo(allSpark)
    }

    @Test
    fun `give has AllSpark when request all transformer then get it from server`() {
        val allSpark = "token"
        val transformer = Transformer.create(name = "A", team = Team.Decepticons)
        val mockResponse = MockResponse().apply {
            setBody(gson.toJson(listOf(transformer)))
            setHeader("Content-Type", "application/json")
        }
        get<MockWebServer>().enqueue(mockResponse)
        val remote = RemoteDataSourceImpl(get())
        val response = runBlocking {
            remote.setAllSpark(allSpark)
            remote.getTransformers()
        }
        val request = get<MockWebServer>().takeRequest()
        Truth.assertThat(request.path).isEqualTo("/transformers")
        Truth.assertThat(request.headers["Authorization"]).isEqualTo("Bearer $allSpark")
        Truth.assertThat(response).containsExactly(transformer)
    }

    @Test
    fun `give has AllSpark when create transformer then post it to server`() {
        val allSpark = "token"
        val transformer = Transformer.create(name = "A", team = Team.Decepticons)
        val mockResponse = MockResponse().apply {
            setBody(gson.toJson(transformer.copy(id = "id", teamIcon = "icon_url")))
            setHeader("Content-Type", "application/json")
        }
        get<MockWebServer>().enqueue(mockResponse)
        val remote = RemoteDataSourceImpl(get())
        val response = runBlocking {
            remote.setAllSpark(allSpark)
            remote.createTransformer(transformer)
        }
        val request = get<MockWebServer>().takeRequest()
        Truth.assertThat(request.path).isEqualTo("/transformers")
        Truth.assertThat(request.headers["Authorization"]).isEqualTo("Bearer $allSpark")
        Truth.assertThat(response).isEqualTo(transformer.copy(id = "id", teamIcon = "icon_url"))
    }

    @Test
    fun `give has AllSpark when delete transformer then remove it from server`() {
        val allSpark = "token"
        val removeId = "id"
        val mockResponse = MockResponse()
        get<MockWebServer>().enqueue(mockResponse)
        val remote = RemoteDataSourceImpl(get())
        runBlocking {
            remote.setAllSpark(allSpark)
            remote.deleteTransformer(removeId)
        }
        val request = get<MockWebServer>().takeRequest()
        Truth.assertThat(request.path).isEqualTo("/transformers/$removeId")
        Truth.assertThat(request.headers["Authorization"]).isEqualTo("Bearer $allSpark")
    }

    @After
    fun dropdown() {
        get<MockWebServer>().shutdown()
    }
}
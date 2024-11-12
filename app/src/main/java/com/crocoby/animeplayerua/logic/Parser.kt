package com.crocoby.animeplayerua.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.crocoby.animeplayerua.AnimeEpisode
import com.crocoby.animeplayerua.AnimeInfo
import com.crocoby.animeplayerua.AnimeItem
import com.crocoby.animeplayerua.utils.UrlEncoderUtil
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.roundToInt

val parser = Parser()

@Composable
fun runParser(
    function: suspend Parser.() -> Unit,
    onError: (ex: Exception) -> Unit
) {
    LaunchedEffect(true) {
        launch {
            try {
                function(parser)
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }
}

data class MainPageAnime(val bestSeason: List<AnimeItem>, val new: List<AnimeItem>)

class Parser {
    private val client: HttpClient = HttpClient()
    private val userAgent: String = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"

    private var userHash: String = ""

    private fun newUserHash(body: String) {
        for (line in body.split("\n")) {
            if (line.contains("var dle_login_hash")) {
                val split = line.split("'")
                if (split.count() == 3) {
                    userHash = split[1]
                }
            }
        }
    }

    private fun parseAnimeSlugFromUrl(url: String): String {
        return url.split("/").last().substringBefore(".html")
    }

    suspend fun getAnimeMainPage(): MainPageAnime {
        val resp = client.get("https://anitube.in.ua") {
            expectSuccess = true

            header("User-Agent", userAgent)
        }
        val body = resp.bodyAsText()
        newUserHash(body)

        val document = Ksoup.parse(body)
        val content = document.body().getElementsByClass("content")[0]

        val bestSeason = ArrayList<AnimeItem>()
        val new = ArrayList<AnimeItem>()

        // Best
        val liElements = content.getElementsByClass("portfolio_items")[0].getElementsByTag("li")
        for (liElement in liElements) {
            val name = liElement.getElementsByClass("text_content")[0].text()
            val img = "https://anitube.in.ua" + liElement.getElementsByTag("img")[0].attr("src")
            val slug = parseAnimeSlugFromUrl(liElement.getElementsByTag("a")[0].attr("href"))

            bestSeason.add(AnimeItem(slug, name, img))
        }

        // New
        val els = content.getElementsByClass("box lcol")[0].getElementsByClass("news_2")
        for (el in els) {
            val titleEl = el.getElementsByTag("a")[0]

            val name = titleEl.text()
            val img = "https://anitube.in.ua" + el.getElementsByTag("img")[0].attr("src")
            val slug = parseAnimeSlugFromUrl(titleEl.attr("href"))

            new.add(AnimeItem(slug, name, img))
        }

        return MainPageAnime(bestSeason.toList(), new.toList())
    }

    suspend fun searchAnimeByName(query: String): List<AnimeItem> {
        val resp = client.post("https://anitube.in.ua/engine/ajax/controller.php?mod=search") {
            expectSuccess = true

            contentType(ContentType.Application.FormUrlEncoded)
            header("User-Agent", userAgent)
            setBody("query=${UrlEncoderUtil.encode(query)}&user_hash=$userHash")
        }
        val body = resp.bodyAsText()

        val document = Ksoup.parse(body)
        val content = document.body()

        val result = ArrayList<AnimeItem>()

        val els = content.getElementsByTag("a")
        for (el in els) {
            val name = el.getElementsByClass("searchheading_title")[0].text()
            val img = el.getElementsByTag("img")[0].attr("src")
            val slug = parseAnimeSlugFromUrl(el.attr("href"))

            result.add(AnimeItem(slug, name, img))
        }

        return result.toList()
    }

    suspend fun getAnimeItemBySlug(slug: String): AnimeItem {
        val resp = client.get("https://anitube.in.ua/$slug.html") {
            expectSuccess = true

            header("User-Agent", userAgent)
        }
        val body = resp.bodyAsText()
        newUserHash(body)

        val document = Ksoup.parse(body)
        val content = document.body()

        val name = content.getElementsByTag("h2")[0].text()
        val imageUrl = "https://anitube.in.ua" + content.getElementsByClass("story_post")[0].getElementsByTag("img")[0].attr("src")

        return AnimeItem(slug, name, imageUrl)
    }

    suspend fun getAnimeInfoBySlug(slug: String): AnimeInfo {
        val resp = client.get("https://anitube.in.ua/$slug.html") {
            expectSuccess = true

            header("User-Agent", userAgent)
        }
        val body = resp.bodyAsText()
        newUserHash(body)

        val document = Ksoup.parse(body)
        val content = document.body()

        val name = content.getElementsByTag("h2")[0].text()
        val imageUrl = "https://anitube.in.ua" + content.getElementsByClass("story_post")[0].getElementsByTag("img")[0].attr("src")
        val description = content.getElementsByClass("my-text")[0].text()
        val rate = content.getElementsByClass("div1")[0].getElementsByTag("span")[0].text().toFloat().roundToInt()

        // Parsing episodes
        val newsId = slug.substringBefore('-')
        val playlists = HashMap<String, String>()
        val episodes = ArrayList<AnimeEpisode>()

        if (body.contains("RalodePlayer.init(")) {
            val epSplit = body.split("\n")
            val line = epSplit.find {
                it.contains("RalodePlayer.init(")
            }!!

            val jsonStr = "[${line.substringAfter("(").substringBeforeLast(")").substringBeforeLast(",")}]"
            val json = Json.parseToJsonElement(jsonStr).jsonArray
            val players = json[0].jsonArray.map { it.jsonPrimitive.content }
            for ((index, player) in players.withIndex()) {
                val id = "0_$index"
                playlists[id] = player

                val rawEpisodes = json[1].jsonArray[index].jsonArray
                for (rawEpisode in rawEpisodes) {
                    val name = rawEpisode.jsonObject["name"]!!.jsonPrimitive.content

                    val codePart = Ksoup.parse(rawEpisode.jsonObject["code"]!!.jsonPrimitive.content)
                    val url = codePart.attr("src")

                    episodes.add(AnimeEpisode(name, url, id))
                }
            }
        } else {
            val epResp = client.get("https://anitube.in.ua/engine/ajax/playlists.php?news_id=$newsId&xfield=playlist&user_hash=$userHash") {
                expectSuccess = true

                header("User-Agent", userAgent)
            }

            val epDocument = Ksoup.parse(Json.parseToJsonElement(epResp.bodyAsText()).jsonObject["response"]!!.jsonPrimitive.content)

            for (li in epDocument.getElementsByClass("playlists-lists")[0].getElementsByTag("li")) {
                playlists[li.attr("data-id")] = li.text()
            }

            for (li in epDocument.getElementsByClass("playlists-videos")[0].getElementsByTag("li")) {
                episodes.add(
                    AnimeEpisode(
                        li.text(),
                        li.attr("data-file"),
                        li.attr("data-id")
                    )
                )
            }
        }

        return AnimeInfo(
            slug, name, imageUrl, description, rate, playlists, episodes.toList()
        )
    }

    suspend fun getDirectUrlFromIFrame(url: String): String {
        val resp = client.get(url) {
            expectSuccess = true

            header("User-Agent", userAgent)
        }
        val body = resp.bodyAsText()
        val fileUrl = body.split("\n").find {
            it.contains("file:") || it.contains("src: ")
        }!!

        return fileUrl.substringBeforeLast("\"").substringAfterLast("\"").substringAfterLast(",").substringAfterLast("]")
    }
}
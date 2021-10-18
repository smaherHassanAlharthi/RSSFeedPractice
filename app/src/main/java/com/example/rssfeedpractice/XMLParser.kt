package com.example.rssfeedpractice

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.URL
import java.util.*

data class Feeds(val title: String?,val id: URL, val name: String?,val uri:String, val published: String,
                 val summary:String) {
    override fun toString(): String = title!!
}


class XMLParser {
    private val ns: String? = null

    fun parse(inputStream: InputStream): List<Feeds> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readRssFeed(parser)
        }
    }

    private fun readRssFeed(parser: XmlPullParser): List<Feeds> {

        val feeds = mutableListOf<Feeds>()

        parser.require(XmlPullParser.START_TAG, ns, "feed")

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "entry") {
                parser.require(XmlPullParser.START_TAG, ns, "entry")
                var title: String? = null
                var id: URL? = null
                var name: String? = null
                var uri:String? =null
                var published: String? = null
                var summary: String? = null

                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.eventType != XmlPullParser.START_TAG) {
                        continue
                    }
                    when (parser.name) {
                        "id" ->  id = URL(readId(parser))
                        "title" -> title = readTitle(parser)
                        "published" -> published = readPublished(parser)
                        "summary" -> summary = readSummary(parser)
                        "author" -> {
                            parser.require(XmlPullParser.START_TAG, ns, "author")
                            while (parser.next() != XmlPullParser.END_TAG) {
                                if (parser.eventType != XmlPullParser.START_TAG) {
                                    continue
                                }
                                when (parser.name) {
                                    "name" -> name = readName(parser)
                                    "uri" -> uri = readUri(parser)
                                    else -> skip(parser) //skipping the next
                                }
                            }
                        }
                        else -> skip(parser)
                    }
                }
                feeds.add(Feeds(title ,id!!,name,uri!!,published!!,summary!!))
            } else {
                skip(parser)
            }
        }
        return feeds
    }

    private fun readUri(parser: XmlPullParser): String? {
        parser.require(XmlPullParser.START_TAG, ns, "uri")
        val uri = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "uri")
        return uri
    }

    private fun readTitle(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "title")
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "title")
        return title
    }

    private fun readId(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "id")
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "id")
        return id
    }


    private fun readName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "name")
        val name = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "name")
        return name
    }


    private fun readPublished(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "published")
        val published = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "published")
        return published
    }
    private fun readSummary(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "summary")
        val summary = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "summary")
        return summary
    }


    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
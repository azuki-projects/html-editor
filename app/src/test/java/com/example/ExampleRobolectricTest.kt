package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.editor.DomParser
import com.example.editor.HtmlFormatter
import com.example.editor.HtmlLinter
import com.example.editor.WebGuides
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WebForge", appName)
  }

  @Test
  fun `formatter indents html properly`() {
    val raw = "<div><p>Hello World</p></div>"
    val formatted = HtmlFormatter.formatHtml(raw, 2)
    assertTrue(formatted.contains("  <p>Hello World</p>"))
  }

  @Test
  fun `linter detects unclosed tag`() {
    val badHtml = "<div><p>Unclosed paragraph</div>"
    val issues = HtmlLinter.lint(badHtml)
    assertTrue(issues.any { it.rule == "unclosed-tag" })
  }

  @Test
  fun `dom parser generates tree nodes`() {
    val sample = "<html><body><h1>Title</h1></body></html>"
    val nodes = DomParser.parse(sample)
    assertTrue(nodes.isNotEmpty())
    assertEquals("html", nodes.first().tagName)
  }

  @Test
  fun `guides are loaded with web cheat sheets`() {
    assertTrue(WebGuides.GUIDES.isNotEmpty())
    assertTrue(WebGuides.GUIDES.any { it.category == "HTML5" })
    assertTrue(WebGuides.GUIDES.any { it.category == "CSS" })
    assertTrue(WebGuides.GUIDES.any { it.category == "JavaScript" })
  }
}

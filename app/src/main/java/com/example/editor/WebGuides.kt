package com.example.editor

data class WebGuideItem(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val codeSnippet: String,
    val tips: List<String>
)

object WebGuides {

    val GUIDES: List<WebGuideItem> = listOf(
        WebGuideItem(
            id = "html5_semantics",
            category = "HTML5",
            title = "HTML5 Semantic Structure",
            description = "Structure pages for accessibility, screen readers, and SEO using native landmark elements instead of unstyled divs.",
            codeSnippet = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Semantic Landmark Example</title>
</head>
<body>
  <!-- Header with brand & primary navigation -->
  <header>
    <h1>Brand Logo</h1>
    <nav aria-label="Main Navigation">
      <ul>
        <li><a href="#home">Home</a></li>
        <li><a href="#features">Features</a></li>
      </ul>
    </nav>
  </header>

  <!-- Main content landmark -->
  <main>
    <article>
      <h2>Article Title</h2>
      <p>Self-contained editorial or blog post content.</p>
    </article>
  </main>

  <!-- Footer landmark -->
  <footer>
    <p>&copy; 2026 Made with Azuki. All rights reserved.</p>
  </footer>
</body>
</html>""",
            tips = listOf(
                "Always include <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> for mobile responsiveness.",
                "Use exactly one <main> landmark per HTML document.",
                "Use <nav> for primary navigation menus and include aria-label when using multiple nav elements."
            )
        ),
        WebGuideItem(
            id = "css_flexbox",
            category = "CSS",
            title = "Flexbox Alignment & Layout",
            description = "Master one-dimensional layouts with flexbox, centering elements effortlessly both horizontally and vertically.",
            codeSnippet = """.flex-container {
  display: flex;
  flex-direction: row;      /* or column */
  justify-content: center;   /* align along main axis */
  align-items: center;       /* align along cross axis */
  gap: 16px;                 /* clean spacing between items */
  flex-wrap: wrap;           /* wrap onto next line on small screens */
}

.flex-item {
  flex: 1 1 280px;           /* grow, shrink, min-basis */
  padding: 20px;
  background: #1e293b;
  border-radius: 12px;
}""",
            tips = listOf(
                "Use 'gap' instead of margins on children for modern, reliable spacing.",
                "To perfectly center anything: set 'display: flex; justify-content: center; align-items: center;'.",
                "Use 'flex: 1' to make cards or columns stretch equally across available space."
            )
        ),
        WebGuideItem(
            id = "css_grid",
            category = "CSS",
            title = "Modern CSS Grid",
            description = "Two-dimensional layout engine for dashboards, responsive galleries, and app interfaces without media queries.",
            codeSnippet = """.grid-layout {
  display: grid;
  /* Auto-fit columns with minimum 260px width */
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  padding: 20px;
}

.card-hero {
  grid-column: span 2; /* Span across two columns on wider screens */
}""",
            tips = listOf(
                "repeat(auto-fit, minmax(260px, 1fr)) creates an intrinsically responsive grid with zero media queries.",
                "Combine grid-template-rows and grid-template-columns for complex dashboard cards.",
                "Use 'fr' fractional units to divide remaining free space proportionally."
            )
        ),
        WebGuideItem(
            id = "js_dom_events",
            category = "JavaScript",
            title = "DOM Manipulation & Event Listeners",
            description = "Interact with user clicks, handle form submissions, and mutate the DOM dynamically with modern ES6+.",
            codeSnippet = """// Select element
const submitBtn = document.querySelector('#submitBtn');
const inputField = document.querySelector('#usernameInput');
const outputList = document.querySelector('#outputList');

// Attach Event Listener
submitBtn.addEventListener('click', (event) => {
  event.preventDefault();
  const value = inputField.value.trim();

  if (!value) return;

  // Create new DOM element
  const li = document.createElement('li');
  li.textContent = value;
  li.classList.add('list-item');

  // Append to parent
  outputList.appendChild(li);
  inputField.value = '';

  console.log('Item added successfully:', value);
});""",
            tips = listOf(
                "Always call 'event.preventDefault()' when handling form submits to prevent page reloading.",
                "Prefer 'addEventListener' over inline HTML 'onclick' attributes for separation of concerns.",
                "Use 'element.classList.add()', 'remove()', and 'toggle()' instead of raw className editing."
            )
        ),
        WebGuideItem(
            id = "responsive_design",
            category = "Best Practices",
            title = "Responsive Web Design & Breakpoints",
            description = "Strategies to ensure web projects render beautifully on mobile phones, tablets, and desktop displays.",
            codeSnippet = """/* Mobile First Base Styles (default for phones) */
body {
  font-size: 16px;
  padding: 16px;
}

/* Tablet breakpoint */
@media (min-width: 640px) {
  .container {
    max-width: 600px;
    margin: 0 auto;
  }
}

/* Desktop breakpoint */
@media (min-width: 1024px) {
  .container {
    max-width: 960px;
  }
  .sidebar-layout {
    display: grid;
    grid-template-columns: 280px 1fr;
  }
}""",
            tips = listOf(
                "Adopt Mobile-First approach: write default CSS for small viewports first, then scale up using min-width media queries.",
                "Ensure interactive touch targets (buttons, links) are at least 44px by 44px.",
                "Always set img { max-width: 100%; height: auto; } so media never overflows parent containers."
            )
        ),
        WebGuideItem(
            id = "seo_meta",
            category = "SEO & Meta",
            title = "Meta Tags, Social Graph & Favicon",
            description = "Complete boilerplate for OpenGraph preview cards, mobile theme color, and search ranking meta tags.",
            codeSnippet = """<!-- Primary Meta Tags -->
<title>Product Title — WebForge Studio</title>
<meta name="title" content="Product Title — WebForge Studio">
<meta name="description" content="Build and design responsive websites live on Android. Made with Azuki.">

<!-- Theme Color for mobile status bar -->
<meta name="theme-color" content="#0f172a">

<!-- Open Graph / Facebook / Discord -->
<meta property="og:type" content="website">
<meta property="og:title" content="WebForge Studio">
<meta property="og:description" content="Build and design responsive websites live on Android.">

<!-- Twitter Cards -->
<meta property="twitter:card" content="summary_large_image">""",
            tips = listOf(
                "Keep title tags under 60 characters to prevent truncation in search result pages.",
                "Meta description should be between 120 and 155 characters.",
                "Setting 'theme-color' gives browsers a branded status bar tint on mobile."
            )
        )
    )
}

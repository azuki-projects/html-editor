package com.example.editor

data class QuickSnippet(
    val label: String,
    val textToInsert: String,
    val cursorOffsetFromEnd: Int = 0 // Offset from end of textToInsert to position cursor
)

data class CdnLibrary(
    val id: String,
    val name: String,
    val description: String,
    val tagToInject: String,
    val category: String
)

data class StarterTemplate(
    val id: String,
    val title: String,
    val badge: String,
    val description: String,
    val htmlContent: String
)

object CodeSnippets {

    val QUICK_KEYS: List<QuickSnippet> = listOf(
        QuickSnippet("<>", "<>", 1),
        QuickSnippet("</>", "</>", 1),
        QuickSnippet("=\"\"", "=\"\"", 1),
        QuickSnippet("class", "class=\"\"", 1),
        QuickSnippet("id", "id=\"\"", 1),
        QuickSnippet("style", "style=\"\"", 1),
        QuickSnippet("<div>", "<div>\n  \n</div>", 7),
        QuickSnippet("<p>", "<p></p>", 4),
        QuickSnippet("<span>", "<span></span>", 7),
        QuickSnippet("<a>", "<a href=\"#\"></a>", 4),
        QuickSnippet("<button>", "<button type=\"button\"></button>", 9),
        QuickSnippet("<h1>", "<h1></h1>", 5),
        QuickSnippet("<h2>", "<h2></h2>", 5),
        QuickSnippet("<img>", "<img src=\"\" alt=\"\" />", 10),
        QuickSnippet("<ul>", "<ul>\n  <li></li>\n</ul>", 12),
        QuickSnippet("<li>", "<li></li>", 5),
        QuickSnippet("<form>", "<form action=\"\" method=\"POST\">\n  \n</form>", 8),
        QuickSnippet("<input>", "<input type=\"text\" placeholder=\"\" />", 4),
        QuickSnippet("<style>", "<style>\n  \n</style>", 9),
        QuickSnippet("<script>", "<script>\n  \n</script>", 10),
        QuickSnippet("<!-- -->", "<!--  -->", 4),
        QuickSnippet("{}", "{}", 1),
        QuickSnippet("()", "()", 1),
        QuickSnippet("[]", "[]", 1),
        QuickSnippet(";", ";", 0),
        QuickSnippet(":", ": ", 0)
    )

    val CDN_LIBRARIES: List<CdnLibrary> = listOf(
        CdnLibrary(
            id = "tailwind",
            name = "Tailwind CSS",
            description = "Utility-first CSS framework via official CDN script",
            tagToInject = "<script src=\"https://cdn.tailwindcss.com\"></script>",
            category = "CSS Framework"
        ),
        CdnLibrary(
            id = "bootstrap",
            name = "Bootstrap 5.3",
            description = "Responsive grid and component library",
            tagToInject = "<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\"></script>",
            category = "CSS Framework"
        ),
        CdnLibrary(
            id = "fontawesome",
            name = "Font Awesome 6",
            description = "Icon library with thousands of web icons",
            tagToInject = "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css\">",
            category = "Icons"
        ),
        CdnLibrary(
            id = "google_font_inter",
            name = "Google Font (Inter)",
            description = "Modern sans-serif typeface for UI design",
            tagToInject = "<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n<link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap\" rel=\"stylesheet\">\n<style>body { font-family: 'Inter', sans-serif; }</style>",
            category = "Typography"
        ),
        CdnLibrary(
            id = "animate_css",
            name = "Animate.css",
            description = "Ready-to-use cross-browser CSS animations",
            tagToInject = "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css\"/>",
            category = "Animation"
        ),
        CdnLibrary(
            id = "chart_js",
            name = "Chart.js",
            description = "Simple yet flexible JavaScript charting for designers & developers",
            tagToInject = "<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>",
            category = "Data Visualization"
        ),
        CdnLibrary(
            id = "confetti",
            name = "Canvas Confetti",
            description = "Celebration particle effect library",
            tagToInject = "<script src=\"https://cdn.jsdelivr.net/npm/canvas-confetti@1.9.2/dist/confetti.browser.min.js\"></script>",
            category = "Effects"
        )
    )

    val STARTER_TEMPLATES: List<StarterTemplate> = listOf(
        StarterTemplate(
            id = "hero_landing",
            title = "Modern SaaS Landing",
            badge = "Responsive",
            description = "Clean gradient hero section, CTA buttons, feature cards, and modern styling.",
            htmlContent = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SaaS Product Studio</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background: #0f172a;
      color: #f8fafc;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 32px 16px;
    }
    .badge {
      display: inline-block;
      padding: 6px 14px;
      background: rgba(56, 189, 248, 0.15);
      color: #38bdf8;
      border: 1px solid rgba(56, 189, 248, 0.3);
      border-radius: 9999px;
      font-size: 13px;
      font-weight: 600;
      margin-bottom: 20px;
      letter-spacing: 0.5px;
    }
    h1 {
      font-size: 2.2rem;
      text-align: center;
      max-width: 600px;
      line-height: 1.25;
      background: linear-gradient(135deg, #ffffff 30%, #94a3b8);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      margin-bottom: 16px;
    }
    p.subtitle {
      color: #94a3b8;
      text-align: center;
      max-width: 480px;
      line-height: 1.6;
      font-size: 1rem;
      margin-bottom: 28px;
    }
    .cta-btn {
      background: linear-gradient(135deg, #3b82f6, #06b6d4);
      color: #ffffff;
      border: none;
      padding: 14px 28px;
      font-size: 15px;
      font-weight: 600;
      border-radius: 12px;
      cursor: pointer;
      box-shadow: 0 10px 25px -5px rgba(59, 130, 246, 0.5);
      transition: transform 0.2s, box-shadow 0.2s;
    }
    .cta-btn:active {
      transform: scale(0.97);
    }
    .features {
      margin-top: 48px;
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 16px;
      max-width: 800px;
      width: 100%;
    }
    .card {
      background: rgba(30, 41, 59, 0.7);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 16px;
      padding: 24px;
      backdrop-filter: blur(8px);
    }
    .card h3 {
      font-size: 1.1rem;
      color: #38bdf8;
      margin-bottom: 8px;
    }
    .card p {
      font-size: 0.9rem;
      color: #94a3b8;
      line-height: 1.5;
    }
  </style>
</head>
<body>
  <div class="badge">🚀 NEXT GENERATION DEVTOOLS</div>
  <h1>Build Faster With Intelligent Web Tools</h1>
  <p class="subtitle">An ultra-lightweight environment engineered for real-time creativity, instant previews, and seamless workflows.</p>
  <button class="cta-btn" onclick="celebrate()">Launch Workspace</button>

  <div class="features">
    <div class="card">
      <h3>⚡ Instant Live View</h3>
      <p>Render changes in milliseconds with sandboxed responsive previews.</p>
    </div>
    <div class="card">
      <h3>🔍 Deep DOM Inspection</h3>
      <p>Explore node hierarchies and edit attributes directly in real time.</p>
    </div>
    <div class="card">
      <h3>🛠 Built-in REPL</h3>
      <p>Debug with a live JavaScript console and interactive command prompt.</p>
    </div>
  </div>

  <script>
    function celebrate() {
      console.log('Launch Workspace button clicked!');
      alert('Welcome to WebForge Studio!');
    }
  </script>
</body>
</html>"""
        ),
        StarterTemplate(
            id = "todo_app",
            title = "Interactive Todo App",
            badge = "JavaScript DOM",
            description = "Fully functional Todo list with state, add, delete, and completion toggles.",
            htmlContent = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quick Tasks</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background: #18181b;
      color: #fafafa;
      display: flex;
      justify-content: center;
      padding: 30px 16px;
    }
    .app-container {
      width: 100%;
      max-width: 440px;
      background: #27272a;
      border: 1px solid #3f3f46;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.4);
    }
    h2 {
      font-size: 1.4rem;
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .count-pill {
      font-size: 12px;
      background: #3f3f46;
      color: #a1a1aa;
      padding: 4px 10px;
      border-radius: 12px;
    }
    .input-row {
      display: flex;
      gap: 8px;
      margin-bottom: 20px;
    }
    input[type="text"] {
      flex: 1;
      padding: 12px 14px;
      border-radius: 10px;
      background: #18181b;
      border: 1px solid #3f3f46;
      color: #fafafa;
      font-size: 14px;
      outline: none;
    }
    input[type="text"]:focus {
      border-color: #a855f7;
    }
    button.add-btn {
      padding: 12px 18px;
      background: #a855f7;
      color: white;
      border: none;
      border-radius: 10px;
      font-weight: 600;
      cursor: pointer;
    }
    ul { list-style: none; display: flex; flex-direction: column; gap: 8px; }
    li {
      background: #18181b;
      padding: 12px 14px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border: 1px solid #3f3f46;
      transition: all 0.2s;
    }
    .task-left {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
    }
    .checkbox {
      width: 18px;
      height: 18px;
      border-radius: 6px;
      border: 2px solid #71717a;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    li.done .checkbox {
      background: #10b981;
      border-color: #10b981;
    }
    li.done .checkbox::after {
      content: '✓';
      color: white;
      font-size: 11px;
    }
    li.done .text {
      text-decoration: line-through;
      color: #71717a;
    }
    .delete-btn {
      background: none;
      border: none;
      color: #ef4444;
      font-size: 16px;
      cursor: pointer;
      padding: 4px 8px;
    }
  </style>
</head>
<body>
  <div class="app-container">
    <h2>
      <span>My Tasks</span>
      <span class="count-pill" id="counter">0 tasks</span>
    </h2>
    <div class="input-row">
      <input type="text" id="taskInput" placeholder="Add a new task..." onkeypress="handleKey(event)">
      <button class="add-btn" onclick="addTask()">Add</button>
    </div>
    <ul id="taskList"></ul>
  </div>

  <script>
    let tasks = [
      { id: 1, text: 'Design responsive layout', done: true },
      { id: 2, text: 'Test in mobile & tablet viewport', done: false },
      { id: 3, text: 'Inspect DOM node hierarchy', done: false }
    ];

    function render() {
      const list = document.getElementById('taskList');
      list.innerHTML = '';
      tasks.forEach(t => {
        const li = document.createElement('li');
        if (t.done) li.classList.add('done');
        li.innerHTML = `
          <div class="task-left" onclick="toggleTask(${'$'}{t.id})">
            <div class="checkbox"></div>
            <span class="text">${'$'}{t.text}</span>
          </div>
          <button class="delete-btn" onclick="deleteTask(${'$'}{t.id})">✕</button>
        `;
        list.appendChild(li);
      });
      document.getElementById('counter').innerText = `${'$'}{tasks.filter(t => !t.done).length} remaining`;
      console.log('Tasks updated, count:', tasks.length);
    }

    function addTask() {
      const input = document.getElementById('taskInput');
      const text = input.value.trim();
      if (!text) return;
      tasks.push({ id: Date.now(), text, done: false });
      input.value = '';
      render();
    }

    function toggleTask(id) {
      tasks = tasks.map(t => t.id === id ? { ...t, done: !t.done } : t);
      render();
    }

    function deleteTask(id) {
      tasks = tasks.filter(t => t.id !== id);
      render();
    }

    function handleKey(e) {
      if (e.key === 'Enter') addTask();
    }

    render();
  </script>
</body>
</html>"""
        ),
        StarterTemplate(
            id = "particle_canvas",
            title = "Interactive Canvas Particles",
            badge = "HTML5 Canvas",
            description = "Interactive physics particles that respond to touch and mouse movement.",
            htmlContent = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Particle Canvas</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      background: #020617;
      overflow: hidden;
      height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      font-family: sans-serif;
    }
    canvas {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 1;
    }
    .hud {
      position: relative;
      z-index: 10;
      text-align: center;
      color: #94a3b8;
      pointer-events: none;
    }
    .hud h1 {
      color: #f8fafc;
      font-size: 1.8rem;
      margin-bottom: 8px;
      letter-spacing: 1px;
    }
    .hud p { font-size: 0.9rem; }
  </style>
</head>
<body>
  <canvas id="particleCanvas"></canvas>
  <div class="hud">
    <h1>NEURAL PARTICLES</h1>
    <p>Tap or move across screen to interact</p>
  </div>

  <script>
    const canvas = document.getElementById('particleCanvas');
    const ctx = canvas.getContext('2d');
    let width = canvas.width = window.innerWidth;
    let height = canvas.height = window.innerHeight;

    window.addEventListener('resize', () => {
      width = canvas.width = window.innerWidth;
      height = canvas.height = window.innerHeight;
    });

    const particles = [];
    const count = 60;
    const colors = ['#38bdf8', '#818cf8', '#c084fc', '#f472b6'];

    for (let i = 0; i < count; i++) {
      particles.push({
        x: Math.random() * width,
        y: Math.random() * height,
        vx: (Math.random() - 0.5) * 1.5,
        vy: (Math.random() - 0.5) * 1.5,
        radius: Math.random() * 3 + 1.5,
        color: colors[Math.floor(Math.random() * colors.length)]
      });
    }

    let pointer = { x: width / 2, y: height / 2 };
    window.addEventListener('pointermove', (e) => {
      pointer.x = e.clientX;
      pointer.y = e.clientY;
    });

    function animate() {
      ctx.fillStyle = 'rgba(2, 6, 23, 0.2)';
      ctx.fillRect(0, 0, width, height);

      for (let i = 0; i < particles.length; i++) {
        const p = particles[i];
        p.x += p.vx;
        p.y += p.vy;

        if (p.x < 0 || p.x > width) p.vx *= -1;
        if (p.y < 0 || p.y > height) p.vy *= -1;

        // Draw particle
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
        ctx.fillStyle = p.color;
        ctx.shadowBlur = 10;
        ctx.shadowColor = p.color;
        ctx.fill();

        // Connect lines between nearby particles
        for (let j = i + 1; j < particles.length; j++) {
          const p2 = particles[j];
          const dist = Math.hypot(p.x - p2.x, p.y - p2.y);
          if (dist < 90) {
            ctx.beginPath();
            ctx.moveTo(p.x, p.y);
            ctx.lineTo(p2.x, p2.y);
            ctx.strokeStyle = `rgba(148, 163, 184, ${'$'}{1 - dist / 90 * 0.8})`;
            ctx.lineWidth = 0.5;
            ctx.stroke();
          }
        }
      }

      requestAnimationFrame(animate);
    }

    animate();
    console.log('Particles canvas initialized successfully');
  </script>
</body>
</html>"""
        ),
        StarterTemplate(
            id = "neon_card",
            title = "Animated Cyber Neon Card",
            badge = "CSS FX",
            description = "Glassmorphic cyber card with glowing rainbow border animation and responsive layout.",
            htmlContent = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cyber Neon Card</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      background: #09090b;
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      padding: 20px;
    }
    .neon-box {
      position: relative;
      width: 100%;
      max-width: 360px;
      padding: 3px;
      border-radius: 24px;
      background: linear-gradient(60deg, #f79533, #f37055, #ef4e7b, #a166ab, #5073b8, #1098ad, #07b39b, #6fba82);
      background-size: 300% 300%;
      animation: animatedgradient 5s ease infinite;
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.6);
    }
    @keyframes animatedgradient {
      0% { background-position: 0% 50%; }
      50% { background-position: 100% 50%; }
      100% { background-position: 0% 50%; }
    }
    .inner-card {
      background: #121216;
      border-radius: 21px;
      padding: 32px 24px;
      color: #fafafa;
      text-align: center;
    }
    .icon-badge {
      width: 56px;
      height: 56px;
      border-radius: 16px;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.1);
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      margin-bottom: 20px;
    }
    h2 { font-size: 1.5rem; margin-bottom: 10px; font-weight: 700; }
    p { font-size: 0.95rem; color: #a1a1aa; line-height: 1.6; margin-bottom: 24px; }
    .btn {
      width: 100%;
      padding: 14px;
      background: #fafafa;
      color: #09090b;
      font-weight: 600;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      transition: opacity 0.2s;
    }
    .btn:active { opacity: 0.8; }
  </style>
</head>
<body>
  <div class="neon-box">
    <div class="inner-card">
      <div class="icon-badge">✨</div>
      <h2>Dynamic Shimmer</h2>
      <p>High performance CSS animated gradient border utilizing hardware-accelerated transforms.</p>
      <button class="btn" onclick="alert('Neon Card Activated!')">Explore Feature</button>
    </div>
  </div>
</body>
</html>"""
        ),
        StarterTemplate(
            id = "clean_html5",
            title = "Semantic HTML5 Starter",
            badge = "Clean Boilerplate",
            description = "Standard accessible HTML5 template with semantic landmarks: header, nav, main, footer.",
            htmlContent = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Clean Semantic Document</title>
  <style>
    body {
      font-family: system-ui, -apple-system, sans-serif;
      line-height: 1.6;
      max-width: 680px;
      margin: 0 auto;
      padding: 24px 16px;
      color: #1f2937;
      background: #f9fafb;
    }
    header { border-bottom: 2px solid #e5e7eb; padding-bottom: 16px; margin-bottom: 24px; }
    nav ul { list-style: none; display: flex; gap: 16px; padding: 0; margin-top: 12px; }
    nav a { color: #2563eb; text-decoration: none; font-weight: 500; }
    main { margin-bottom: 32px; }
    article { background: white; padding: 24px; border-radius: 12px; border: 1px solid #e5e7eb; }
    footer { border-top: 1px solid #e5e7eb; padding-top: 16px; text-align: center; color: #6b7280; font-size: 14px; }
  </style>
</head>
<body>
  <header>
    <h1>WebForge Document</h1>
    <nav>
      <ul>
        <li><a href="#about">About</a></li>
        <li><a href="#articles">Articles</a></li>
        <li><a href="#contact">Contact</a></li>
      </ul>
    </nav>
  </header>

  <main>
    <article>
      <h2>Welcome to Semantic Web</h2>
      <p>This layout uses clean HTML5 tags for accessibility, search engines, and maintainable styling.</p>
    </article>
  </main>

  <footer>
    <p>&copy; 2026 WebForge Studio. All rights reserved.</p>
  </footer>
</body>
</html>"""
        )
    )
}

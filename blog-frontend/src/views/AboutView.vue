<template>
  <div class="site-shell about-page">
    <SiteHeader />

    <main class="page-main about-main">
      <div class="container about-container">
        <h2 class="page-title">关于本站</h2>

        <section class="about-card surface-card">
          <h3 class="about-title">项目简介</h3>
          <p class="about-text">
            这是一个基于 <strong>Spring Boot 3</strong> 和 <strong>Vue 3</strong> 构建的现代个人博客系统，
            旨在提供一个简洁、高效、美观的知识分享平台。
          </p>
          <p class="about-text">
            项目采用前后端分离架构：后端提供 RESTful API，前端采用单页应用（SPA）模式，
            以保证良好的用户体验与系统性能。
          </p>
        </section>

        <section class="about-card surface-card">
          <h3 class="about-title">核心技术栈</h3>
          <div class="tech-stack">
            <div class="tech-item">
              <h4 class="tech-name">后端（Backend）</h4>
              <ul class="tech-list">
                <li><strong>Spring Boot</strong>：核心框架</li>
                <li><strong>MyBatis-Plus</strong>：ORM 框架</li>
                <li><strong>MySQL</strong>：关系型数据库</li>
                <li><strong>Spring Security + JWT</strong>：安全认证</li>
                <li><strong>Lombok</strong>：代码简化</li>
              </ul>
            </div>
            <div class="tech-item">
              <h4 class="tech-name">前端（Frontend）</h4>
              <ul class="tech-list">
                <li><strong>Vue 3</strong>：渐进式框架</li>
                <li><strong>Vite</strong>：构建工具</li>
                <li><strong>Pinia</strong>：状态管理</li>
                <li><strong>Vue Router</strong>：路由管理</li>
                <li><strong>Element Plus</strong>：UI 组件库</li>
                <li><strong>SCSS</strong>：CSS 预处理器</li>
              </ul>
            </div>
          </div>
        </section>

        <section class="about-card surface-card">
          <h3 class="about-title">联系作者</h3>
          <p class="about-text">如果你对本项目有建议或问题，欢迎联系我：</p>
          <ul class="contact-list">
            <li>邮箱：<a href="mailto:admin@example.com">admin@example.com</a></li>
            <li>
              GitHub：
              <a href="https://github.com/example/blog" target="_blank" rel="noreferrer">https://github.com/example/blog</a>
            </li>
          </ul>
        </section>
      </div>
    </main>

    <footer class="site-footer">
      <div class="container">
        <p class="footer-text">© 2026 个人博客 - Powered by Spring Boot & Vue 3</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import SiteHeader from '../components/SiteHeader.vue'
</script>

<style scoped lang="scss">
.about-container {
  max-width: 920px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 32px;
  text-align: center;
}

.about-card {
  padding: 24px;
  margin-bottom: 16px;
}

.about-title {
  margin: 0 0 12px;
  font-size: 22px;
}

.about-text {
  margin: 0 0 10px;
  color: #475569;
  line-height: 1.8;
}

.tech-stack {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.tech-item {
  border: 1px solid var(--border);
  border-radius: 14px;
  padding: 16px;
  background: #fff;
}

.tech-name {
  margin: 0 0 10px;
  font-size: 16px;
}

.tech-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.tech-list li {
  position: relative;
  margin-bottom: 8px;
  padding-left: 18px;
  color: #475569;
}

.tech-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #6366f1;
}

.contact-list {
  margin: 0;
  padding-left: 16px;
  color: #475569;
}

.contact-list li {
  margin-bottom: 8px;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 26px;
  }

  .about-card {
    padding: 18px;
  }

  .tech-stack {
    grid-template-columns: 1fr;
  }
}
</style>

// ═══════════════════════════════════════════════
// EG MobileMoney Asist – Landing Page JavaScript
// ═══════════════════════════════════════════════

let APK_URL = "https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-/releases/latest/download/EG_MobileMoney_Asist.apk";
// Detect environment to provide direct, hassle-free download links
if (window.location.hostname.includes("github.io")) {
  // If hosted on GitHub Pages, use the relative path to the committed APK in docs/landing/
  const pathParts = window.location.pathname.split('/');
  pathParts[pathParts.length - 1] = "EG_MobileMoney_Asist.apk";
  APK_URL = window.location.origin + pathParts.join('/');
} else if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1" || window.location.protocol === "file:") {
  // For local testing, point directly to the local file
  APK_URL = "EG_MobileMoney_Asist.apk";
}
const GITHUB_URL = "https://github.com/Tranquilino1/EGMobileMoneyAsist-AI-";

// ── Navbar scroll effect ──
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
  if (window.scrollY > 50) {
    navbar.classList.add('scrolled');
  } else {
    navbar.classList.remove('scrolled');
  }
}, { passive: true });

// ── Mobile menu toggle ──
const mobileToggle = document.getElementById('mobileToggle');
const navLinks = document.querySelector('.nav-links');
if (mobileToggle && navLinks) {
  mobileToggle.addEventListener('click', () => {
    const isOpen = navLinks.style.display === 'flex';
    if (isOpen) {
      navLinks.style.display = 'none';
      navLinks.style.position = '';
    } else {
      navLinks.style.display = 'flex';
      navLinks.style.flexDirection = 'column';
      navLinks.style.position = 'absolute';
      navLinks.style.top = '70px';
      navLinks.style.left = '0';
      navLinks.style.right = '0';
      navLinks.style.background = 'rgba(7,13,24,0.98)';
      navLinks.style.padding = '16px 24px';
      navLinks.style.borderBottom = '1px solid rgba(255,255,255,0.08)';
      navLinks.style.zIndex = '999';
    }
  });
}

// ── Smooth scroll for anchor links ──
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', function(e) {
    const targetId = this.getAttribute('href');
    if (targetId === '#') return;
    const target = document.querySelector(targetId);
    if (target) {
      e.preventDefault();
      const navHeight = 70;
      const targetPos = target.getBoundingClientRect().top + window.scrollY - navHeight;
      window.scrollTo({ top: targetPos, behavior: 'smooth' });
      // Close mobile menu if open
      if (navLinks && navLinks.style.display === 'flex' && navLinks.style.position === 'absolute') {
        navLinks.style.display = 'none';
        navLinks.style.position = '';
      }
    }
  });
});

// ── QR Code Generation ──
function generateQRCode() {
  const container = document.getElementById('qr-container');
  if (!container) return;

  // Clear loading state
  container.innerHTML = '';

  try {
    if (typeof QRCode !== 'undefined') {
      // Use qrcodejs library
      new QRCode(container, {
        text: APK_URL,
        width: 180,
        height: 180,
        colorDark: "#0a1628",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
      });
    } else {
      // Fallback: Use QR Server API
      const img = document.createElement('img');
      img.src = `https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent(APK_URL)}&bgcolor=ffffff&color=0a1628&margin=10`;
      img.alt = 'QR Code para descargar EG MobileMoney Asist APK';
      img.style.borderRadius = '8px';
      img.style.width = '180px';
      img.style.height = '180px';
      container.appendChild(img);
    }
  } catch (err) {
    // Final fallback
    const img = document.createElement('img');
    img.src = `https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent(APK_URL)}&bgcolor=ffffff&color=0a1628&margin=10`;
    img.alt = 'QR Code APK Download';
    img.style.borderRadius = '8px';
    img.style.width = '180px';
    img.style.height = '180px';
    container.appendChild(img);
  }
}

// ── Intersection Observer for animations ──
function setupAnimations() {
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = '1';
        entry.target.style.transform = 'translateY(0)';
        observer.unobserve(entry.target);
      }
    });
  }, observerOptions);

  // Animate feature cards
  document.querySelectorAll('.feature-card').forEach((card, i) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(30px)';
    card.style.transition = `opacity 0.5s ease ${i * 0.1}s, transform 0.5s ease ${i * 0.1}s`;
    observer.observe(card);
  });

  // Animate AI feature items
  document.querySelectorAll('.ai-feature-item').forEach((item, i) => {
    item.style.opacity = '0';
    item.style.transform = 'translateX(-20px)';
    item.style.transition = `opacity 0.5s ease ${i * 0.15}s, transform 0.5s ease ${i * 0.15}s`;
    observer.observe(item);
  });

  // Animate tech items
  document.querySelectorAll('.tech-item').forEach((item, i) => {
    item.style.opacity = '0';
    item.style.transform = 'translateX(20px)';
    item.style.transition = `opacity 0.4s ease ${i * 0.08}s, transform 0.4s ease ${i * 0.08}s`;
    observer.observe(item);
  });
}

// ── Stats counter animation ──
function animateCounter(element, target, duration = 1500) {
  const start = 0;
  const startTime = performance.now();
  const isPercent = target.toString().includes('%');
  const isCurrency = target.toString().includes('€') || target.toString().includes('$');
  const numericTarget = parseFloat(target.toString().replace(/[^0-9.]/g, ''));

  if (isNaN(numericTarget) || numericTarget === 0) return;

  const animate = (currentTime) => {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const eased = 1 - Math.pow(1 - progress, 3); // ease out cubic
    const current = Math.round(eased * numericTarget);

    if (isPercent) element.textContent = current + '%';
    else if (isCurrency) element.textContent = current + '€';
    else element.textContent = current;

    if (progress < 1) requestAnimationFrame(animate);
  };

  requestAnimationFrame(animate);
}

// ── Particle background effect ──
function createParticles() {
  const hero = document.querySelector('.hero');
  if (!hero) return;

  for (let i = 0; i < 20; i++) {
    const particle = document.createElement('div');
    particle.style.cssText = `
      position: absolute;
      width: ${Math.random() * 3 + 1}px;
      height: ${Math.random() * 3 + 1}px;
      background: rgba(0, 230, 118, ${Math.random() * 0.4 + 0.1});
      border-radius: 50%;
      top: ${Math.random() * 100}%;
      left: ${Math.random() * 100}%;
      pointer-events: none;
      z-index: 0;
      animation: particleFloat ${Math.random() * 6 + 4}s ease-in-out infinite;
      animation-delay: ${Math.random() * 4}s;
    `;
    hero.appendChild(particle);
  }

  // Add particle keyframes
  const style = document.createElement('style');
  style.textContent = `
    @keyframes particleFloat {
      0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.4; }
      25% { transform: translate(${Math.random() * 40 - 20}px, ${Math.random() * 40 - 20}px) scale(1.2); opacity: 0.7; }
      75% { transform: translate(${Math.random() * 40 - 20}px, ${Math.random() * 40 - 20}px) scale(0.8); opacity: 0.2; }
    }
  `;
  document.head.appendChild(style);
}

// ── Copy to clipboard for QR URL ──
function setupCopyLink() {
  const qrWrapper = document.querySelector('.qr-wrapper');
  if (!qrWrapper) return;

  const copyBtn = document.createElement('button');
  copyBtn.textContent = '📋 Copiar enlace';
  copyBtn.style.cssText = `
    background: rgba(0,230,118,0.1);
    border: 1px solid rgba(0,230,118,0.3);
    color: #00e676;
    padding: 6px 14px;
    border-radius: 100px;
    font-size: 0.75rem;
    cursor: pointer;
    font-family: inherit;
    transition: all 0.2s;
  `;
  copyBtn.addEventListener('click', async () => {
    try {
      await navigator.clipboard.writeText(APK_URL);
      copyBtn.textContent = '✅ ¡Copiado!';
      setTimeout(() => { copyBtn.textContent = '📋 Copiar enlace'; }, 2000);
    } catch {
      copyBtn.textContent = '📋 Copiar enlace';
    }
  });
  qrWrapper.appendChild(copyBtn);
}

// ── Initialize everything on DOM ready ──
document.addEventListener('DOMContentLoaded', () => {
  // Small delay to let QR library load
  setTimeout(generateQRCode, 500);
  setupAnimations();
  createParticles();
  setupCopyLink();

  // Animate stats on hero visible
  const statsObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const statValues = entry.target.querySelectorAll('.stat-value');
        statValues.forEach(sv => {
          const text = sv.textContent;
          if (text.includes('%')) animateCounter(sv, text, 1200);
        });
        statsObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.5 });

  const heroStats = document.querySelector('.hero-stats');
  if (heroStats) statsObserver.observe(heroStats);
});

// ── Track download button click ──
document.getElementById('btn-download-apk')?.addEventListener('click', () => {
  console.log('[EG MobileMoney] APK download initiated');
});

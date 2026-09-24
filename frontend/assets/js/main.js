// GLS CONSTRUCTION - Main JavaScript
// Production API configuration
const localPage =
  window.location.protocol === "file:" ||
  ["localhost", "127.0.0.1"].includes(window.location.hostname);
const configuredApiBase = document
  .querySelector('meta[name="gls-api-base"]')
  ?.content.trim()
  .replace(/\/$/, "");
const API_BASE =
  configuredApiBase ||
  (localPage
    ? "http://localhost:8080/api"
    : "https://gls-construction-backend.onrender.com/api");
const placeholder = "Project information coming soon.";

function resolveMediaUrl(url) {
  if (!url) return "";

  if (/^https?:\/\//i.test(url)) {
    return url;
  }

  const backendBase = API_BASE.replace(/\/api\/?$/, "");

  return `${backendBase}${url.startsWith("/") ? url : `/${url}`}`;
}
let csrfTokenPromise;

function getCsrfToken() {
  if (!csrfTokenPromise) {
    csrfTokenPromise = fetch(`${API_BASE}/auth/csrf`, {
      credentials: "include",
      cache: "no-store",
    })
      .then((response) => {
        if (!response.ok)
          throw new Error("Unable to initialize security token");
        return response.json();
      })
      .then((data) => data.token)
      .catch((error) => {
        csrfTokenPromise = undefined;
        throw error;
      });
  }
  return csrfTokenPromise;
}

document.addEventListener("DOMContentLoaded", () => {
  const navLinks = document.querySelectorAll(".navbar-nav .nav-link");
  const anchorLinks = [...navLinks].filter((link) =>
    link.getAttribute("href")?.startsWith("#"),
  );
  const sections = anchorLinks
    .map((link) => document.querySelector(link.getAttribute("href")))
    .filter(Boolean);
  const setActiveLink = (
    sectionId = window.location.hash.slice(1) || "home",
  ) => {
    anchorLinks.forEach((link) =>
      link.classList.toggle(
        "active",
        link.getAttribute("href") === `#${sectionId}`,
      ),
    );
  };
  const updateActiveSection = () => {
    const marker = window.scrollY + 120;
    const currentSection = sections.reduce((current, section) => {
      if (section.offsetTop <= marker) return section;
      return current;
    }, sections[0]);
    if (!currentSection) return;
    setActiveLink(currentSection.id);
    const nextHash = `#${currentSection.id}`;
    if (window.location.hash !== nextHash)
      history.replaceState(null, "", nextHash);
  };
  const scrollToAnchor = (target, duration = 460) => {
    const start = window.scrollY;
    const end = Math.max(
      0,
      target.getBoundingClientRect().top + window.scrollY - 86,
    );
    const distance = end - start;
    const startTime = performance.now();
    const animate = (currentTime) => {
      const progress = Math.min((currentTime - startTime) / duration, 1);
      const easedProgress = 1 - Math.pow(1 - progress, 3);
      window.scrollTo({
        top: start + distance * easedProgress,
        behavior: "auto",
      });
      if (progress < 1) requestAnimationFrame(animate);
    };
    requestAnimationFrame(animate);
  };
  anchorLinks.forEach((link) =>
    link.addEventListener("click", (event) => {
      const target = document.querySelector(link.getAttribute("href"));
      if (
        target &&
        !window.matchMedia("(prefers-reduced-motion: reduce)").matches
      ) {
        event.preventDefault();
        history.pushState(null, "", link.getAttribute("href"));
        setActiveLink(target.id);
        scrollToAnchor(target);
      }
      if (target) setActiveLink(target.id);
      const navbar = document.getElementById("navbarNav");
      if (navbar?.classList.contains("show"))
        bootstrap.Collapse.getOrCreateInstance(navbar).hide();
    }),
  );
  let scrollFrame = null;
  window.addEventListener("scroll", () => {
    if (scrollFrame) return;
    scrollFrame = requestAnimationFrame(() => {
      updateActiveSection();
      scrollFrame = null;
    });
  });
  window.addEventListener("resize", updateActiveSection);
  window.addEventListener("hashchange", () => {
    const sectionId = window.location.hash.slice(1) || "home";
    setActiveLink(sectionId);
  });
  updateActiveSection();
});

document.addEventListener("DOMContentLoaded", () => {
  const revealSelectors = [
    ".hero-section .col-lg-6",
    ".trust-copy",
    ".trust-metrics",
    ".about-visual",
    ".about-copy",
    "main > section:not(.hero-section) > .container > .text-center",
    ".gls-card",
    ".service-flow",
    ".project-carousel",
    "#projectsGrid",
    ".package-panel-left",
    ".package-panel-right",
    ".material-showcase-heading",
    ".material-showcase",
    ".timeline-item",
    "main > section:last-of-type .container",
    ".page-header .container",
    ".trust-metric",
    ".footer .col-lg-4",
    ".footer .col-lg-3",
    ".footer-bottom",
    ".admin-header",
    ".admin-panel",
    ".stat",
  ];
  const revealElements = document.querySelectorAll(revealSelectors.join(","));
  if (!revealElements.length) return;

  revealElements.forEach((element, index) => {
    element.classList.add("scroll-reveal");
    const isRightColumn = element.matches(
      ".hero-section .col-lg-6:nth-child(2), .about-copy, .package-panel-right, .trust-metrics, .project-carousel",
    );
    const isLeftColumn = element.matches(
      ".hero-section .col-lg-6:first-child, .about-visual, .package-panel-left, .trust-copy",
    );
    element.classList.add(
      isRightColumn
        ? "scroll-reveal--right"
        : isLeftColumn
          ? "scroll-reveal--left"
          : "scroll-reveal--up",
    );
    if (
      element.classList.contains("gls-card") ||
      element.classList.contains("timeline-item") ||
      element.classList.contains("stat")
    )
      element.dataset.revealDelay = String(index % 4);
  });

  const showElements = (elements) =>
    elements.forEach((element) => element.classList.add("is-visible"));

  if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
    showElements(revealElements);
    return;
  }

  if (!("IntersectionObserver" in window)) {
    showElements(revealElements);
    return;
  }

  const observer = new IntersectionObserver(
    (entries, revealObserver) => {
      entries.forEach((entry) => {
        entry.target.classList.toggle("is-visible", entry.isIntersecting);
      });
    },
    { rootMargin: "0px 0px -10% 0px", threshold: 0.16 },
  );
  revealElements.forEach((element) => observer.observe(element));
});

document.addEventListener("DOMContentLoaded", () => {
  const metrics = document.querySelectorAll(".trust-metric");
  if (!metrics.length) return;

  const animateCounter = (element) => {
    const target = Number(element.dataset.counterTarget);
    const duration = 900;
    const startTime = performance.now();
    const update = (currentTime) => {
      const progress = Math.min((currentTime - startTime) / duration, 1);
      const easedProgress = 1 - Math.pow(1 - progress, 3);
      element.textContent = Math.floor(easedProgress * target) || 1;
      if (progress < 1) requestAnimationFrame(update);
    };
    requestAnimationFrame(update);
  };

  const revealMetrics = (entries, observer) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      entry.target.classList.add("is-visible");
      animateCounter(entry.target.querySelector(".trust-number"));
      observer.unobserve(entry.target);
    });
  };

  if ("IntersectionObserver" in window) {
    const observer = new IntersectionObserver(revealMetrics, {
      threshold: 0.3,
    });
    metrics.forEach((metric) => observer.observe(metric));
  } else {
    metrics.forEach((metric) => {
      metric.classList.add("is-visible");
      animateCounter(metric.querySelector(".trust-number"));
    });
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const showcase = document.getElementById("projectShowcase");
  const grid = document.getElementById("projectsGrid");
  if (!showcase || !grid) return;
  const elements = {
    title: document.getElementById("showcaseTitle"),
    type: document.getElementById("showcaseType"),
    location: document.getElementById("showcaseLocation"),
    area: document.getElementById("showcaseArea"),
    status: document.getElementById("showcaseStatus"),
    statusText: document.getElementById("showcaseStatusText"),
    description: document.getElementById("showcaseDescription"),
    visual: document.getElementById("showcaseVisual"),
  };
  let lastFocusedProject = null;
  const openShowcase = (event) => {
    const project = event.currentTarget.projectData;
    if (!project) return;
    lastFocusedProject = event.currentTarget;
    elements.title.textContent = project.name || placeholder;
    elements.type.textContent = project.projectType || placeholder;
    elements.location.textContent = project.location || placeholder;
    elements.area.textContent = project.builtUpArea
      ? `${project.builtUpArea} sq.ft`
      : placeholder;
    elements.status.textContent = project.status || placeholder;
    elements.statusText.textContent = project.status || placeholder;
    elements.description.textContent = project.description || placeholder;
    const images = (project.media || []).filter(
      (item) => item.mediaType === "IMAGE",
    );
    const imageUrl = images[0]?.mediaUrl;
    elements.visual.className = "project-showcase-visual";
    elements.visual.innerHTML = imageUrl
      ? `<img src="${resolveMediaUrl(imageUrl)}" alt="${project.name || "Project"} image" loading="lazy" decoding="async">`
      : `<span>Project image coming soon.</span>`;
    const gallery = document.getElementById("showcaseGallery");
    if (gallery)
      gallery.innerHTML =
        images.length > 1
          ? images
              .map(
                (item) =>
                  `<img src="${resolveMediaUrl(item.mediaUrl)}" alt="${item.category} project image" loading="lazy" decoding="async">`,
              )
              .join("")
          : "";
    const latest = (project.progressUpdates || [])
      .slice()
      .sort((a, b) =>
        (b.updateDate || "").localeCompare(a.updateDate || ""),
      )[0];
    const progressTitle = document.getElementById("showcaseProgressTitle");
    const progressImage = document.getElementById("showcaseProgressImage");
    const progressVideo = document.getElementById("showcaseProgressVideo");
    if (progressTitle)
      progressTitle.textContent =
        latest?.title || "Project Progress / Latest Update";
    if (progressImage)
      progressImage.innerHTML = latest?.imageUrl
        ? `<img src="${resolveMediaUrl(latest.imageUrl)}" alt="Latest project progress" loading="lazy" decoding="async">`
        : "Project image coming soon.";
    if (progressVideo)
      progressVideo.innerHTML = latest?.videoUrl
        ? `<video controls src="${resolveMediaUrl(latest.videoUrl)}"></video>`
        : "Project video coming soon.";
    showcase.classList.add("is-open");
    showcase.setAttribute("aria-hidden", "false");
    document.body.classList.add("project-showcase-open");
    showcase.querySelector(".project-showcase-dialog").focus();
  };
  const closeShowcase = () => {
    showcase.classList.remove("is-open");
    showcase.setAttribute("aria-hidden", "true");
    document.body.classList.remove("project-showcase-open");
    lastFocusedProject?.focus();
  };
  const renderProjects = (projects) => {
    if (!projects.length) {
      grid.innerHTML = `<div class="col-12 text-center text-muted py-5">${placeholder}</div>`;
      return;
    }
    grid.innerHTML = projects
      .map((project) => {
        const image = (project.media || []).find(
          (item) => item.mediaType === "IMAGE" && item.mediaUrl,
        );
        const visual = image
          ? `<div class="project-visual project-visual-uploaded"><img src="${resolveMediaUrl(image.mediaUrl)}" alt="${project.name || "Project"} image" loading="lazy" decoding="async"><span class="project-visual-label">${project.projectType || "Project"}</span></div>`
          : `<div class="project-visual project-visual-villa"><span class="project-visual-label">${project.projectType || "Project"}</span></div>`;
        return `<div class="col-lg-4 col-md-6"><button class="gls-card project-card p-0 h-100 overflow-hidden" type="button" aria-label="Open project showcase">${visual}<div class="p-4"><div class="mb-2"><span class="badge ${project.status === "COMPLETED" ? "bg-success" : "bg-warning text-dark"}">${project.status || "Status coming soon"}</span></div><h4 class="mb-3">${project.name || placeholder}</h4><ul class="list-unstyled text-muted small mb-3"><li class="mb-1"><strong>📍 Location:</strong> ${project.location || placeholder}</li><li class="mb-1"><strong>📐 Area:</strong> ${project.builtUpArea ? `${project.builtUpArea} sq.ft` : placeholder}</li><li class="mb-1"><strong>🏗️ Type:</strong> ${project.projectType || placeholder}</li></ul><p class="text-muted small mb-0">${project.description || placeholder}</p></div></button></div>`;
      })
      .join("");
    grid.querySelectorAll(".project-card").forEach((card, index) => {
      card.projectData = projects[index];
      card.addEventListener("click", openShowcase);
    });
  };
  fetch(`${API_BASE}/projects`, { cache: "no-store", credentials: "include" })
    .then((response) => (response.ok ? response.json() : Promise.reject()))
    .then(renderProjects)
    .catch(() => {
      grid.innerHTML = `<div class="col-12 text-center text-muted py-5">${placeholder}</div>`;
    });
  showcase
    .querySelectorAll("[data-project-close]")
    .forEach((button) => button.addEventListener("click", closeShowcase));
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && showcase.classList.contains("is-open"))
      closeShowcase();
  });
});

function calculateHomeCost() {
  const sqftInput = document.getElementById("sqftArea")?.value;
  const floors = Number(document.getElementById("homeCalcFloors")?.value || 1);
  const rate = Number(
    document.getElementById("homeCalcPackage")?.value || 2100,
  );
  const resultBox = document.getElementById("calc-result");
  const priceDisplay = document.getElementById("calc-price");
  if (sqftInput && sqftInput > 0) {
    const floorMultiplier = 1 + (floors - 1) * 0.5;
    priceDisplay.innerText = new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(sqftInput * rate * floorMultiplier);
    document
      .getElementById("home-calculator-card")
      ?.classList.add("is-flipped");
    resultBox.setAttribute("aria-hidden", "false");
  } else alert("Please enter a valid built-up area.");
}

document.addEventListener("DOMContentLoaded", () => {
  const calculator = document.getElementById("home-calculator");
  const card = document.getElementById("home-calculator-card");
  const result = document.getElementById("calc-result");
  const closeResult = document.getElementById("calc-result-close");
  calculator?.addEventListener("submit", (event) => {
    event.preventDefault();
    calculateHomeCost();
  });
  closeResult?.addEventListener("click", () => {
    card?.classList.remove("is-flipped");
    result?.setAttribute("aria-hidden", "true");
  });
});

function calculateFullCost() {
  const lengthInput = document.getElementById("calcLength");
  const widthInput = document.getElementById("calcWidth");
  const floorsInput = document.getElementById("calcFloors");
  const packageInput = document.getElementById("calcPackage");
  if (!lengthInput || !widthInput || !floorsInput || !packageInput) return;
  const length = Number(lengthInput.value);
  const width = Number(widthInput.value);
  const floors = Number(floorsInput.value);
  const rate = Number(packageInput.value);
  if (!(length > 0 && width > 0 && floors > 0 && rate > 0)) return;
  const totalArea = length * width * floors;
  const total = totalArea * rate;
  const formatCurrency = (value) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(value);
  document.getElementById("calc-built-area").textContent =
    `${totalArea.toLocaleString("en-IN")} sq.ft`;
  document.getElementById("calc-rate").textContent =
    `${formatCurrency(rate)} / sq.ft`;
  document.getElementById("full-calc-price").textContent =
    formatCurrency(total);
  [
    ["structural", 0.5],
    ["finishing", 0.35],
    ["labour", 0.15],
  ].forEach(([name, percentage]) => {
    document.getElementById(`breakdown-${name}-price`).textContent =
      formatCurrency(total * percentage);
    document.getElementById(`breakdown-${name}-bar`).style.width =
      `${percentage * 100}%`;
  });
}

function calculateFullCostFromSubmit(event) {
  event.preventDefault();
  calculateFullCost();
}

function exportEstimate() {
  window.print();
}

document.addEventListener("DOMContentLoaded", () => {
  const calculator = document.getElementById("full-calculator");
  if (!calculator) return;
  calculator.addEventListener("submit", calculateFullCostFromSubmit);
  ["calcLength", "calcWidth", "calcFloors", "calcPackage"].forEach((id) =>
    document.getElementById(id)?.addEventListener("input", calculateFullCost),
  );
  calculator
    .querySelector(".calculator-print-button")
    ?.addEventListener("click", exportEstimate);
  calculateFullCost();
});

document.addEventListener("DOMContentLoaded", () => {
  const contactForm = document.getElementById("contactForm");
  if (!contactForm) return;
  contactForm.addEventListener("submit", (event) => {
    event.preventDefault();
    const submitBtn = document.getElementById("submitBtn");
    const statusDiv = document.getElementById("formStatus");
    const requiredFields = ["name", "phone", "location", "message"];
    if (
      requiredFields.some(
        (fieldId) => !document.getElementById(fieldId).value.trim(),
      )
    ) {
      statusDiv.innerHTML =
        '<div class="alert alert-warning">Please complete all required fields before submitting.</div>';
      statusDiv.style.display = "block";
      return;
    }
    submitBtn.disabled = true;
    submitBtn.innerText = "Sending...";
    const areaValue = document.getElementById("area").value.trim();
    const enquiryData = {
      name: document.getElementById("name").value.trim(),
      phone: document.getElementById("phone").value.trim(),
      location: document.getElementById("location").value.trim(),
      projectType: document.getElementById("projectType").value,
      area: areaValue === "" ? null : Number(areaValue),
      message: document.getElementById("message").value.trim(),
    };
    getCsrfToken()
      .then((csrfToken) =>
        fetch(`${API_BASE}/enquiries`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken,
          },
          credentials: "include",
          body: JSON.stringify(enquiryData),
        }),
      )
      .then((response) => {
        if (!response.ok) throw new Error();
        statusDiv.innerHTML =
          '<div class="alert alert-success">Thank you! Your enquiry has been sent successfully. We will contact you soon.</div>';
        contactForm.reset();
      })
      .catch((error) => {
        console.error("Enquiry submission failed:", error);
        statusDiv.innerHTML =
          '<div class="alert alert-danger">The enquiry service is temporarily unavailable. Please ensure the backend is running, then try again or call/WhatsApp us directly.</div>';
      })
      .finally(() => {
        statusDiv.style.display = "block";
        submitBtn.disabled = false;
        submitBtn.innerText = "Send Enquiry";
      });
  });
});

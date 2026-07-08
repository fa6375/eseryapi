(function () {
  "use strict";

  /* ---------------- Header scroll state ---------------- */
  var header = document.querySelector(".site-header");
  function onScroll() {
    if (!header) return;
    if (window.scrollY > 40) header.classList.add("is-scrolled");
    else header.classList.remove("is-scrolled");
  }
  document.addEventListener("scroll", onScroll, { passive: true });
  onScroll();

  /* ---------------- Mobile nav toggle ---------------- */
  var navToggle = document.querySelector(".nav-toggle");
  var mainNav = document.querySelector(".main-nav");
  if (navToggle && mainNav) {
    navToggle.addEventListener("click", function () {
      mainNav.classList.toggle("is-open");
      var expanded = mainNav.classList.contains("is-open");
      navToggle.setAttribute("aria-expanded", expanded ? "true" : "false");
    });
    mainNav.querySelectorAll("a").forEach(function (link) {
      link.addEventListener("click", function () {
        mainNav.classList.remove("is-open");
      });
    });
  }

  /* ---------------- Scroll reveal ---------------- */
  var revealTargets = document.querySelectorAll(".reveal, .reveal-stagger");
  if ("IntersectionObserver" in window && revealTargets.length) {
    var io = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            entry.target.classList.add("is-visible");
            io.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.15, rootMargin: "0px 0px -60px 0px" }
    );
    revealTargets.forEach(function (el) {
      io.observe(el);
    });
  } else {
    revealTargets.forEach(function (el) {
      el.classList.add("is-visible");
    });
  }

  /* ---------------- Animated stat counters ---------------- */
  var counters = document.querySelectorAll("[data-count-to]");
  function animateCounter(el) {
    var target = parseFloat(el.getAttribute("data-count-to"));
    if (isNaN(target)) return;
    var duration = 1400;
    var start = null;
    function step(ts) {
      if (start === null) start = ts;
      var progress = Math.min((ts - start) / duration, 1);
      var eased = 1 - Math.pow(1 - progress, 3);
      var value = Math.round(eased * target);
      el.textContent = value;
      if (progress < 1) window.requestAnimationFrame(step);
      else el.textContent = target;
    }
    window.requestAnimationFrame(step);
  }
  if ("IntersectionObserver" in window && counters.length) {
    var counterIo = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            animateCounter(entry.target);
            counterIo.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.4 }
    );
    counters.forEach(function (el) {
      counterIo.observe(el);
    });
  } else {
    counters.forEach(function (el) {
      el.textContent = el.getAttribute("data-count-to");
    });
  }

  /* ---------------- Project gallery lightbox ---------------- */
  var galleryLinks = Array.prototype.slice.call(document.querySelectorAll("[data-lightbox]"));
  var lightbox = document.querySelector(".lightbox");
  if (galleryLinks.length && lightbox) {
    var lightboxImg = lightbox.querySelector("img");
    var current = 0;

    function openAt(index) {
      current = (index + galleryLinks.length) % galleryLinks.length;
      lightboxImg.src = galleryLinks[current].getAttribute("href");
      lightboxImg.alt = galleryLinks[current].getAttribute("data-caption") || "";
      lightbox.classList.add("is-open");
      document.body.style.overflow = "hidden";
    }
    function close() {
      lightbox.classList.remove("is-open");
      document.body.style.overflow = "";
    }
    galleryLinks.forEach(function (link, index) {
      link.addEventListener("click", function (e) {
        e.preventDefault();
        openAt(index);
      });
    });
    var closeBtn = lightbox.querySelector(".lightbox-close");
    if (closeBtn) closeBtn.addEventListener("click", close);
    var prevBtn = lightbox.querySelector(".lightbox-prev");
    var nextBtn = lightbox.querySelector(".lightbox-next");
    if (prevBtn) prevBtn.addEventListener("click", function () { openAt(current - 1); });
    if (nextBtn) nextBtn.addEventListener("click", function () { openAt(current + 1); });
    lightbox.addEventListener("click", function (e) {
      if (e.target === lightbox) close();
    });
    document.addEventListener("keydown", function (e) {
      if (!lightbox.classList.contains("is-open")) return;
      if (e.key === "Escape") close();
      if (e.key === "ArrowLeft") openAt(current - 1);
      if (e.key === "ArrowRight") openAt(current + 1);
    });
  }

  /* ---------------- Admin: multi-image upload preview ---------------- */
  var imageInput = document.querySelector("#project-images-input");
  var previewGrid = document.querySelector("#image-preview-grid");
  var dropZone = document.querySelector("#upload-drop");
  if (imageInput && previewGrid) {
    function renderPreviews(files) {
      previewGrid.querySelectorAll(".js-new-preview").forEach(function (n) { n.remove(); });
      Array.prototype.forEach.call(files, function (file) {
        if (!file.type.startsWith("image/")) return;
        var reader = new FileReader();
        reader.onload = function (e) {
          var fig = document.createElement("figure");
          fig.className = "js-new-preview";
          var img = document.createElement("img");
          img.src = e.target.result;
          fig.appendChild(img);
          previewGrid.appendChild(fig);
        };
        reader.readAsDataURL(file);
      });
    }
    imageInput.addEventListener("change", function () {
      renderPreviews(imageInput.files);
    });
    if (dropZone) {
      ["dragenter", "dragover"].forEach(function (evt) {
        dropZone.addEventListener(evt, function (e) {
          e.preventDefault();
          dropZone.classList.add("is-drag");
        });
      });
      ["dragleave", "drop"].forEach(function (evt) {
        dropZone.addEventListener(evt, function (e) {
          e.preventDefault();
          dropZone.classList.remove("is-drag");
        });
      });
      dropZone.addEventListener("drop", function (e) {
        var files = e.dataTransfer.files;
        imageInput.files = files;
        renderPreviews(files);
      });
      // Note: no manual click-to-open handler here — the <label for="..."> element
      // already opens the file picker natively; adding imageInput.click() on top of
      // that would fire the dialog twice.
    }
  }

  /* ---------------- Confirm-before-submit: works on <form data-confirm> and on
     standalone <button data-confirm> (e.g. multi-action buttons using formaction) ---------------- */
  document.querySelectorAll("form[data-confirm]").forEach(function (form) {
    form.addEventListener("submit", function (e) {
      if (!window.confirm(form.getAttribute("data-confirm"))) {
        e.preventDefault();
      }
    });
  });
  document.querySelectorAll("button[data-confirm]").forEach(function (btn) {
    btn.addEventListener("click", function (e) {
      if (!window.confirm(btn.getAttribute("data-confirm"))) {
        e.preventDefault();
      }
    });
  });
})();

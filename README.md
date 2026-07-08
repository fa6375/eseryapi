# Eser Yapı MMC — Corporate Website (Spring Boot)

A professional, animated corporate website for a facade / aluminum joinery / general
contracting company, with a full admin panel for managing projects (with multiple photos,
location and description) and partner logos.

## Stack

- **Java 17**, **Spring Boot 3.2.5**
- **Spring MVC + Thymeleaf** for server-rendered pages
- **Spring Data JPA + H2** (file-based, zero external DB setup required)
- **Spring Security** for the admin panel (form login)
- Plain **CSS + vanilla JS** for the animations (scroll reveals, animated counters, an
  animated SVG "blueprint" hero, image lightbox, drag-and-drop image upload preview) —
  no frontend build step needed.

## Running it

You'll need **JDK 17+** and **Maven** installed locally (this project was built in a
sandboxed environment without access to Maven Central, so it has **not** been
compile-tested end-to-end — see "A note on verification" below).

```bash
cd construction-website
mvn spring-boot:run
```

Then open **http://localhost:8080**.

The H2 database file is created automatically at `./data/eseryapi.mv.db` the first
time you run the app, and uploaded photos are stored under `./uploads/`. Both persist
across restarts as long as you run the app from the same working directory.

## Admin panel

Go to **http://localhost:8080/admin/login**.

- Username: `admin`
- Password: `EserYapi2026!`

**Change these before deploying anywhere public** — edit `app.admin.username` /
`app.admin.password` in `src/main/resources/application.properties`.

From the admin panel you can:
- **Projects** — add/edit/delete projects with a title, category, location (plus optional
  lat/long for an auto-embedded map on the project page), year, status, area, a long
  description, and **multiple photos** (drag-and-drop or click to upload; each existing
  photo can be removed individually when editing).
- **Partners** — add/edit/delete partner or supplier logos shown on the homepage and the
  public Partners page.
- **Messages** — read and manage inquiries submitted through the public Contact form.

The public **Projects** page and homepage are intentionally empty until you add your
first project — they show a designed "empty state" rather than placeholder content.

## Project structure

```
src/main/java/com/eseryapi/construction/
  config/        Spring Security + static resource (uploads) configuration
  model/         Project, ProjectImage, Partner, ContactMessage, ProjectStatus
  repository/    Spring Data JPA repositories
  service/       Business logic + file storage for uploaded images
  controller/    Public site controllers
  controller/admin/  Admin panel controllers (project/partner/message CRUD)

src/main/resources/
  templates/     Thymeleaf pages (+ fragments/ for shared header, footer, admin sidebar)
  static/css/    Design system + all page styling and animations
  static/js/     Scroll reveals, counters, lightbox, upload previews
  application.properties
```

## Design notes

The visual language is built around the company's actual specialty — facade and
curtain-wall systems — rather than generic stock construction imagery: the hero animates
a blueprint-style facade grid drawing itself in on load, and the same grid motif recurs
through the about/services sections. Palette is graphite/steel with a cool "glass" cyan
and a safety-amber accent; type pairs Space Grotesk (display), Inter (body) and IBM Plex
Mono (labels/data), all loaded from Google Fonts.

## A note on verification

This project was generated in a sandboxed build environment whose network access is
restricted to a small allow-list of domains that does **not** include Maven Central, so
`mvn compile` could not be run here to prove the build green end-to-end. Every file was
checked by hand for structural correctness instead (balanced braces/tags in every Java
and HTML file, valid JS syntax, well-formed XML in `pom.xml`, matching Thymeleaf
attribute/property names against the entity getters/setters, and a known Spring data-
binding pitfall — a file-upload field colliding with the `Project.images` collection —
was found and fixed). Still, please run `mvn spring-boot:run` yourself and let me know if
anything doesn't come up cleanly so it can be fixed quickly.

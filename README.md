# ESER YAPI — website

A single-page site for Eser Yapı with a scroll-driven construction film, light and dark themes, and Azerbaijani / English text. No framework and no dependencies to install.

## Run it

Open `index.html` in a browser — that is enough.

To serve it like a real site instead:

```
npm start        # http://localhost:3000
```

## Deploy on Vercel

1. Push this folder to a GitHub repository.
2. In Vercel choose **Add New → Project** and import the repository.
3. Leave every setting as it is and press **Deploy**. `vercel.json` already tells Vercel what to do.

During the build, Vercel runs `scripts/fetch-images.mjs`, which copies the logo and all project photos from eseryapi.az into `assets/`, so the live site serves them itself.

## Photos and logo

The site looks for each photo in `assets/images/` first. If a file is not there, it loads the same photo from eseryapi.az. If that fails too, the slot shows a styled tile with the project name, never a broken image.

- `npm run images` downloads everything into `assets/images/` and `assets/logo/` on your own computer. Commit the result if you want the photos stored in the repository.
- The logo is `assets/logo/eser-yapi-logo.png`. Replace that file to change it. Until the file exists, the header shows the name "ESER YAPI" as text.
- To use a different photo, put the file in `assets/images/` under the same name as the one it replaces.

## What is where

```
index.html                 the whole site: markup, styles, 3D film and page logic
assets/animations/         still frames of the film (page backgrounds, and the fallback when 3D is unavailable)
assets/fonts/              Inter Tight and Newsreader (with Azerbaijani letters)
assets/images/             project photos (filled by `npm run images` or the Vercel build)
assets/logo/               logo and favicon (filled the same way)
assets/vendor/three.min.js 3D library (MIT licence)
scripts/fetch-images.mjs   the photo downloader
vercel.json, package.json  deployment settings
```

## Editing

Everything is in `index.html`, in this order: styles, page markup, the film (`createFilm`), page logic.

- **Text:** Azerbaijani is written directly in the markup. English is the `EN` list in the page logic. Each piece of text is tied to its translation by its `data-i18n` name.
- **Projects and services:** the `PROJECTS` and `WAYS` lists in the page logic.
- **Colours:** the variables at the top of the styles — `:root` for dark, `[data-theme="light"]` for light.
- **Film:** `KEYS` is the camera path, `PRESETS` the lighting for each theme. The numbers in `apply()` set when each building stage happens (0 = top of the page, 1 = end of the film).

## Notes

- The film is drawn live in 3D and follows the scroll position. On devices without 3D support it switches to the still frames in `assets/animations/`.
- The contact form checks the fields and then opens the visitor's email app with the message addressed to info@eseryapi.az. It does not send email by itself, so no server is needed.
- The chosen theme and language are remembered in the visitor's browser.

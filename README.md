# ESER YAPI — website

A single-page site for Eser Yapı with a scroll-driven, photorealistic construction film, light and dark themes, and Azerbaijani / English text. No framework and no dependencies to install.

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
index.html                 the whole site: markup, styles, the film and page logic
assets/film/               the film: eight photographs of one house from one locked camera, WebP at 1600 and 2560 px wide
docs/film-assets.md        how the film frames were made (prompts, sizes) and how to replace or extend them
assets/fonts/              Inter Tight and Newsreader (with Azerbaijani letters)
assets/images/             project photos (filled by `npm run images` or the Vercel build)
assets/logo/               logo and favicon (filled the same way)
scripts/fetch-images.mjs   the photo downloader
vercel.json, package.json  deployment settings
```

## Editing

Everything is in `index.html`, in this order: styles, page markup, the film (`createFilm`), page logic. There are no libraries: animation is plain CSS plus one scroll loop and one IntersectionObserver.

- **Text:** Azerbaijani is written directly in the markup. English is the `EN` list in the page logic. Each piece of text is tied to its translation by its `data-i18n` name.
- **Projects and services:** the `PROJECTS` and `WAYS` lists in the page logic.
- **Colours:** the variables at the top of the styles — `:root` for dark, `[data-theme="light"]` for light.
- **Film:** `FRAMES` lists the photographs in build order and `CUTS` says where on the scroll (0 = top of the page, 1 = end of the film) each one gives way to the next. The first six are assembled block by block in the order stored in `assets/film/build-N.png` (walls course by course, cladding board by board, scaffolding down from the top, garden outwards); `BUILD` holds the block sizes. After changing a frame, run `python3 scripts/build-order.py` to remake those orders. The last cut, day to dusk, is a dissolve. The caption timings are the `data-from` / `data-to` values on each `.cap`, and `STOPS` in the page logic sets where the stage indicator advances.

## Notes

- The film is drawn on a canvas and follows the scroll position in both directions. The first frame loads at once and the rest in the background, nearest to the scroll position first. Phones get the house in a soft-edged band over a blurred copy of the same photograph, so it is never cropped. With reduced motion switched on, the film still follows the scroll but without easing, and the other entrance animations are skipped.
- The contact form checks the fields and then opens the visitor's email app with the message addressed to info@eseryapi.az. It does not send email by itself, so no server is needed.
- The chosen theme and language are remembered in the visitor's browser.

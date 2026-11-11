# Construction film — how the frames were made

The film is eight photographs of **one house from one locked camera**. They were generated on Higgsfield with **GPT Image 2.6** (`gpt_image_2_6`, 16:9, quality `medium`, resolution `2k` → 2688 × 1520 PNG), then resized to WebP.

| File (`assets/film/`) | Stage shown | Made from |
| --- | --- | --- |
| `01-temel` | 01 Təməl — foundation slab, rebar, excavated pool pit | edit of `07` |
| `02-zemin` | ground-floor columns and half-height brick walls | edit of `03` |
| `03-karkas` | 02 Karkas — concrete frame, brick infill, open timber roof frame, scaffolding | edit of `07` |
| `04-dam` | roof half covered, insulation going on, empty window openings | edit of `05` |
| `05-fasad` | 03 Fasad — roof finished, cladding and render half done, windows in | edit of `07` |
| `06-interyer` | 04 İnteryer — exterior finished, rooms under fit-out, terrace being laid | edit of `07` |
| `07-yasayis` | 05 Yaşayış — finished, furnished, landscaped (**the master image**) | text prompt |
| `08-tehvil` | 06 Təhvil — the same house at dusk, lights on | edit of `07` |

## Why it is consistent

Only `07` was generated from text. Every other frame is an **image-to-image edit** with `07` (or a frame already derived from it) passed as the reference, and a prompt that begins by pinning what must not change:

> Keep the EXACT same camera position, lens, framing, horizon line, sky, hills, background trees and golden-hour sunlight from the left, and the exact same building footprint, position, size, proportions, window positions and gable roof.

…followed by the construction state for that stage. Working **backwards from the finished house** is what keeps the geometry identical: the model removes things from a known building instead of inventing a new one each time.

## Master prompt (`07-yasayis`)

> Photorealistic architectural photograph of a newly completed modern two-storey family house, shot from an elevated three-quarter drone viewpoint about 9 metres above ground at the front-left corner, 35mm lens, camera perfectly level. The house sits exactly in the centre of the frame and occupies roughly the middle 45% of the image width, with generous open lawn and landscape visible on both the left and right sides and in front. Architecture: simple rectangular plan, dark anthracite standing-seam metal gable roof with a slim chimney, upper floor clad in warm natural vertical cedar timber boards with black aluminium framed windows, ground floor in smooth off-white mineral render with three wide floor-to-ceiling black aluminium sliding glass doors showing a softly furnished interior, real glass reflections of the sky. In front of the house a pale limestone terrace with a rectangular swimming pool, two sun loungers, and a few potted olive trees; manicured lawn, gravel path, low planting. Background: gentle rolling green hills with cypress trees and scattered oaks, light haze. Late-afternoon golden-hour sunlight coming from the left, long soft natural shadows falling to the right. High-end real-estate commercial look, no people, no text, not a cartoon, not an illustration, not a toy model.

The house is kept to the middle of the frame on purpose: captions sit either side of it on desktop, and phones show only the middle 58% of the picture.

## Replacing or adding a frame

1. Generate the new image as an edit of `07` (same model and settings), 16:9.
2. Export two sizes next to the others:
   ```
   cwebp -q 80 -m 6 -sharp_yuv -resize 2560 1448 new.png -o assets/film/NN-name-2560.webp
   cwebp -q 78 -m 6 -sharp_yuv -resize 1600 905  new.png -o assets/film/NN-name-1600.webp
   ```
3. Add the name to `FRAMES` and a matching entry to `CUTS` in `index.html`.

## Going further: generated video between stages

Higgsfield's start-frame / end-frame video models (`minimax_h3`, `flux_3_video`) can produce a real time-lapse between two neighbouring frames. Give the earlier frame as `start_image`, the later one as `end_image`, and prompt for a locked-off tripod time-lapse with no camera movement. Extract the frames (`ffmpeg -i clip.mp4 -vf fps=12,scale=1600:-2 f-%03d.webp`), add them to `FRAMES`, and give each a short dissolve in `CUTS`. Seven transitions at about 12.5 credits each is roughly 90 credits; the frames here were made within a 10-credit balance, so the transitions are done in the browser instead.

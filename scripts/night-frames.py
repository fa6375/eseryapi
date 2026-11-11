#!/usr/bin/env python3
"""Makes the dark-theme (night) version of every film frame.

The night frames are graded from the day photographs rather than generated,
so the house, camera and every brick stay exactly where they are — which the
block-by-block build (build-N.png) relies on. The grade:

  * takes the sky straight from the real dusk photograph (08-tehvil), which
    was shot from the same camera, so every frame gets the same blue-hour sky;
  * turns the daylight into cool moonlight (darker, bluer, less saturated);
  * keeps lamps, work lights and lit windows warm and bright;
  * adds the warm pool of a site floodlight around the building.

    python3 scripts/night-frames.py        # needs Pillow and numpy

Writes assets/film/<name>-night-1600.webp and -2560.webp next to the day frames.
"""
from pathlib import Path
import numpy as np
from PIL import Image, ImageFilter

ROOT = Path(__file__).resolve().parent.parent
FILM = ROOT / 'assets' / 'film'
EXTERIOR = ['01-temel', '02-zemin', '03-karkas', '04-dam', '05-fasad', '06-interyer', '07-yasayis']
INTERIOR = ['i1-icerisi', 'i2-qonaq-otagi']
SIZES = [(2560, 1448, 80), (1600, 905, 78)]


def smoothstep(a, b, x):
    t = np.clip((x - a) / (b - a), 0, 1)
    return t * t * (3 - 2 * t)


def load(name, w, h):
    return np.asarray(Image.open(FILM / f'{name}-2560.webp').convert('RGB').resize((w, h), Image.LANCZOS), dtype=np.float32) / 255


def sky_mask(img):
    """Bright, bluish, low-saturation pixels in the upper part of the frame, feathered."""
    h, w, _ = img.shape
    r, g, b = img[..., 0], img[..., 1], img[..., 2]
    lum = 0.2126 * r + 0.7152 * g + 0.0722 * b
    mx, mn = img.max(axis=2), img.min(axis=2)
    sat = (mx - mn) / np.maximum(mx, 1e-4)
    rows = np.linspace(0, 1, h)[:, None]
    m = (lum > 0.55) & (sat < 0.45) & (rows < 0.40)
    m = m | ((rows < 0.14) & (lum > 0.4))                 # top of the frame is always sky
    # sky is one connected region from the top: keep only what touches row 0 column-wise
    keep = np.zeros_like(m)
    keep[0] = m[0]
    for y in range(1, h):
        keep[y] = m[y] & keep[y - 1]
    im = Image.fromarray((keep * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(w / 400))
    return np.asarray(im, dtype=np.float32)[..., None] / 255


def grade_exterior(img, sky):
    h, w, _ = img.shape
    lum = (0.2126 * img[..., 0] + 0.7152 * img[..., 1] + 0.0722 * img[..., 2])[..., None]
    base = lum + (img - lum) * 0.55                       # desaturate
    night = np.power(base, 1.25) * np.array([0.30, 0.36, 0.52])   # moonlight: dark and blue
    # site floodlight: a warm pool around the building
    yy, xx = np.mgrid[0:h, 0:w]
    d = np.sqrt(((xx / w - 0.50) / 0.42) ** 2 + ((yy / h - 0.60) / 0.30) ** 2)
    pool = np.clip(1 - d, 0, 1) ** 1.6
    night = night * (1 + pool[..., None] * np.array([1.35, 0.95, 0.45]))
    m = sky_mask(img)
    # lamps, lit windows and work lights stay warm (only the very brightest points, never the sky)
    glow = smoothstep(0.90, 0.99, lum) * (1 - m)
    night = night + glow * img * np.array([1.0, 0.78, 0.52]) * 0.9
    out = night * (1 - m) + sky * m
    return np.clip(out, 0, 1)


# the three glass doors in the living-room shots (fractions of the frame): that is where the night outside shows
DOORS = [(0.133, 0.229, 0.360, 0.597), (0.392, 0.229, 0.621, 0.597), (0.652, 0.229, 0.881, 0.597)]


def grade_interior(img, sky):
    h, w, _ = img.shape
    lum = (0.2126 * img[..., 0] + 0.7152 * img[..., 1] + 0.0722 * img[..., 2])[..., None]
    view = np.zeros((h, w), np.uint8)
    for x0, y0, x1, y1 in DOORS:
        view[int(y0 * h):int(y1 * h), int(x0 * w):int(x1 * w)] = 255
    view = np.asarray(Image.fromarray(view).filter(ImageFilter.GaussianBlur(w / 900)), dtype=np.float32)[..., None] / 255
    # outside: the same moonlit blue as the exterior frames, with the dusk sky above the hills
    outside = np.power(lum + (img - lum) * 0.5, 1.25) * np.array([0.26, 0.33, 0.52])
    m = sky_mask(img) * view
    outside = outside * (1 - m) + sky * m
    # the room: lamp-lit — darker, warmer, with the light falling off towards the edges
    yy, xx = np.mgrid[0:h, 0:w]
    d = np.sqrt(((xx / w - 0.5) / 0.75) ** 2 + ((yy / h - 0.62) / 0.6) ** 2)
    fall = (0.55 + 0.45 * np.clip(1 - d, 0, 1))[..., None]
    room = np.power(lum * 0.55 + img * 0.45, 1.2) * np.array([0.70, 0.52, 0.34]) * fall
    lamp = smoothstep(0.86, 0.98, lum) * img * np.array([0.55, 0.42, 0.25])
    return np.clip(room * (1 - view) + outside * view + lamp * (1 - view), 0, 1)


def save(arr, name):
    im = Image.fromarray((arr * 255 + 0.5).astype(np.uint8))
    for w, h, q in SIZES:
        im.resize((w, h), Image.LANCZOS).save(FILM / f'{name}-night-{w}.webp', quality=q, method=6)


def main():
    w, h = 2560, 1448
    dusk = load('08-tehvil', w, h)
    sky = dusk                                            # same camera: the dusk photograph's sky lines up with every frame
    for n in EXTERIOR:
        save(grade_exterior(load(n, w, h), sky), n)
        print('night', n)
    for n in INTERIOR:
        if (FILM / f'{n}-2560.webp').exists():
            save(grade_interior(load(n, w, h), sky), n)
            print('night', n)


if __name__ == '__main__':
    main()

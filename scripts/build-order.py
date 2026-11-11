#!/usr/bin/env python3
"""Works out the order in which the construction film assembles each stage.

For every pair of neighbouring frames in assets/film/ it compares the two
photographs block by block (a brick-bond grid) and decides, for each block
where the building changes, WHEN during the scroll that block is put in
place: walls rise course by course, cladding goes on board by board,
scaffolding comes down from the top, the garden spreads out from the house.
Blocks that do not belong to the building (sky, hills, distant trees)
simply blend.

The result is one tiny greyscale PNG per transition, assets/film/build-N.png:
one pixel per block, 0 = blend, 1..255 = start time within the transition.
The grid settings must match BUILD in index.html.

    python3 scripts/build-order.py            # needs Pillow and numpy
    python3 scripts/build-order.py --debug    # also writes overlay previews to /tmp
"""
import sys, random
from pathlib import Path
import numpy as np
from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
FILM = ROOT / 'assets' / 'film'
FRAMES = ['01-temel', '02-zemin', '03-karkas', '04-dam', '05-fasad', '06-interyer', '07-yasayis', '08-tehvil']
W, H = 1600, 905                      # grid is laid out in the coordinates of the 1600 px frames

# where things happen, as fractions of the frame (the house sits in the middle, the pool and site in front)
HOUSE = (0.19, 0.11, 0.82, 0.69)      # x0, y0, x1, y1
FRONT = (0.12, 0.60, 0.97, 0.98)

# per transition: block size, brick bond, how blocks are ordered, how long each block takes to settle
BUILD = [
    dict(cw=22, ch=12, bond=True,  order='courses',   dur=0.10, thr=20, top=0.34),   # foundation -> ground-floor walls
    dict(cw=22, ch=12, bond=True,  order='courses',   dur=0.10, thr=20),   # ground floor -> full shell + roof frame
    dict(cw=22, ch=12, bond=True,  order='roof',      dur=0.10, thr=20),   # shell -> roof sheets + insulation
    dict(cw=12, ch=26, bond=False, order='boards',    dur=0.10, thr=18),   # -> cladding, render, windows
    dict(cw=24, ch=16, bond=True,  order='dismantle', dur=0.12, thr=18),   # scaffolding down, facade finished
    dict(cw=30, ch=20, bond=True,  order='garden',    dur=0.16, thr=16),   # landscaping, pool filled, furniture
]


def grid(cfg):
    cols = -(-W // cfg['cw']) + 1
    rows = -(-H // cfg['ch'])
    return cols, rows


def cell_box(cfg, c, r):
    x = c * cfg['cw'] - (cfg['cw'] // 2 if cfg['bond'] and r % 2 else 0)
    y = r * cfg['ch']
    return x, y, x + cfg['cw'], y + cfg['ch']


def inside(box, zone):
    cx, cy = (box[0] + box[2]) / 2 / W, (box[1] + box[3]) / 2 / H
    return zone[0] <= cx <= zone[2] and zone[1] <= cy <= zone[3]


def load(name):
    return np.asarray(Image.open(FILM / f'{name}-1600.webp').convert('RGB').resize((W, H), Image.LANCZOS), dtype=np.float32)


def plan(i, cfg, a, b):
    rnd = random.Random(1000 + i)
    diff = np.abs(a - b).mean(axis=2)
    cols, rows = grid(cfg)
    cells = []
    o_ = cfg['order']
    for r in range(rows):
        for c in range(cols):
            x0, y0, x1, y1 = cell_box(cfg, c, r)
            box = (max(0, x0), max(0, y0), min(W, x1), min(H, y1))
            if box[2] <= box[0] or box[3] <= box[1]:
                continue
            zone = (HOUSE[0], cfg.get('top', HOUSE[1]), HOUSE[2], HOUSE[3])
            house, front = inside(box, zone), inside(box, FRONT)
            if not (house or front):
                continue
            if diff[box[1]:box[3], box[0]:box[2]].mean() < cfg['thr']:
                continue
            r_, g_, b_ = b[box[1]:box[3], box[0]:box[2]].reshape(-1, 3).mean(axis=0)
            if house and o_ != 'garden' and g_ > r_ * 0.98 and g_ > b_ * 1.08:   # foliage seen through the frame: blend it, don't build it
                continue
            cells.append(dict(c=c, r=r, cx=(box[0] + box[2]) / 2 / W, cy=(box[1] + box[3]) / 2 / H, house=house))
    if not cells:
        return np.zeros((rows, cols), np.uint8), cells

    hc = [k for k in cells if k['house']] or cells
    ymin, ymax = min(k['cy'] for k in hc), max(k['cy'] for k in hc)
    xmin, xmax = min(k['cx'] for k in hc), max(k['cx'] for k in hc)
    def ny(k): return (k['cy'] - ymin) / max(1e-6, ymax - ymin)    # 0 top .. 1 bottom of the building
    def nx(k): return (k['cx'] - xmin) / max(1e-6, xmax - xmin)    # 0 left .. 1 right
    span = 1 - cfg['dur']
    o = cfg['order']
    for k in cells:
        j = rnd.random()
        if not k['house']:                                          # site works in front: one pass across the plot, left to right
            t = 0.10 + 0.72 * (k['cx'] - FRONT[0]) / (FRONT[2] - FRONT[0]) + 0.10 * j if o != 'garden' else None
        else:
            t = None
        if t is None:
            if o == 'courses':                                      # course by course from the ground, each course laid along its length
                row = 1 - ny(k)
                along = nx(k) if int(row * 40) % 2 == 0 else 1 - nx(k)
                t = 0.86 * row + 0.08 * along + 0.06 * j
            elif o == 'roof':                                       # walls get their insulation first, then the roof sheets go on left to right
                if k['cy'] < ymin + 0.33 * (ymax - ymin):
                    t = 0.48 + 0.44 * nx(k) + 0.08 * j
                else:
                    t = 0.5 * (1 - ny(k)) + 0.05 * nx(k) + 0.06 * j
            elif o == 'boards':                                     # cladding board by board, left to right, each board bottom up
                t = 0.80 * nx(k) + 0.12 * (1 - ny(k)) + 0.08 * j
            elif o == 'dismantle':                                  # scaffolding comes down level by level from the top
                t = 0.82 * ny(k) + 0.10 * (nx(k) if int(ny(k) * 12) % 2 else 1 - nx(k)) + 0.08 * j
            elif o == 'garden':                                     # landscaping spreads out from the house
                d = ((k['cx'] - 0.5) ** 2 + ((k['cy'] - 0.64) * 1.6) ** 2) ** 0.5
                t = min(1, d / 0.5) * 0.78 + 0.22 * j
        k['t'] = max(0.0, min(1.0, t)) * span
    out = np.zeros((rows, cols), np.uint8)
    for k in cells:
        out[k['r'], k['c']] = 1 + int(round(k['t'] / span * 254))
    return out, cells


def main():
    debug = '--debug' in sys.argv
    imgs = [load(n) for n in FRAMES[:len(BUILD) + 1]]
    for i, cfg in enumerate(BUILD):
        out, cells = plan(i, cfg, imgs[i], imgs[i + 1])
        Image.fromarray(out).save(FILM / f'build-{i + 1}.png', optimize=True)
        print(f'build-{i + 1}.png  {out.shape[1]}x{out.shape[0]} blocks, {len(cells)} placed')
        if debug:
            vis = imgs[i + 1].copy()
            for k in cells:
                x0, y0, x1, y1 = cell_box(cfg, k['c'], k['r'])
                x0, y0 = max(0, x0), max(0, y0)
                col = np.array([255 * (1 - k['t']), 80, 255 * k['t']])
                vis[y0:y1, x0:x1] = vis[y0:y1, x0:x1] * 0.45 + col * 0.55
            Image.fromarray(vis.clip(0, 255).astype(np.uint8)).resize((800, 452)).save(f'/tmp/claude-501/build-{i + 1}.jpg', quality=80)


if __name__ == '__main__':
    main()

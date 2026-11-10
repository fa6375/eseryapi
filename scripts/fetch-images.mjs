#!/usr/bin/env node
/* Copies every photo the site uses, plus the logo, from eseryapi.az into this project
   (assets/images, assets/logo) so the site serves them itself.

   Runs automatically as the Vercel build step; run it by hand with `npm run images`.
   It never fails the build: any photo it cannot fetch simply keeps loading from
   eseryapi.az at runtime (index.html falls back to the original address by itself). */
import { readFile, writeFile, mkdir, stat } from 'node:fs/promises';

const ROOT = new URL('../', import.meta.url);
const REMOTE = process.env.ESER_REMOTE || 'https://eseryapi.az/wp-content/uploads/';

const html = await readFile(new URL('index.html', ROOT), 'utf8');
const keys = [...new Set(html.match(/20\d\d\/\d\d\/[A-Za-z0-9_.\-]+\.(?:jpe?g|png)/g) || [])];
const jobs = keys.filter((k) => !k.includes('logoyeniweb')).map((k) => ({ url: REMOTE + k, file: 'assets/images/' + k.replace(/\//g, '-') }));
jobs.push({ url: REMOTE + '2022/03/logoyeniweb.png', file: 'assets/logo/eser-yapi-logo.png' });
jobs.push({ url: REMOTE + '2022/03/cropped-favicon-270x270.png', file: 'assets/logo/favicon.png' });

await mkdir(new URL('assets/images/', ROOT), { recursive: true });
await mkdir(new URL('assets/logo/', ROOT), { recursive: true });

let ok = 0, kept = 0; const failed = [];
async function grab(job) {
  const target = new URL(job.file, ROOT);
  try { if ((await stat(target)).size > 0) { kept++; return; } } catch { /* not there yet */ }
  try {
    const res = await fetch(job.url, { signal: AbortSignal.timeout(25000), headers: { 'user-agent': 'eser-yapi-site-build' } });
    const type = res.headers.get('content-type') || '';
    if (!res.ok || !type.startsWith('image/')) throw new Error('HTTP ' + res.status + ' ' + type);
    await writeFile(target, Buffer.from(await res.arrayBuffer()));
    ok++;
  } catch (err) { failed.push(job.url + '  (' + err.message + ')'); }
}
const queue = jobs.slice();
await Promise.all(Array.from({ length: 6 }, async () => { while (queue.length) await grab(queue.shift()); }));

console.log(`images: ${ok} downloaded, ${kept} already present, ${failed.length} failed (of ${jobs.length})`);
if (failed.length) console.log('could not fetch — these keep loading from eseryapi.az:\n  ' + failed.join('\n  '));

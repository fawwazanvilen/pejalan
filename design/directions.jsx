// 6 distinct visual directions — same content, dramatically different aesthetics.
// Each DirectionTile renders a compact phone showing the same result moment
// so the user can compare apples-to-apples.

const PHOTO_ = "assets/mobil-trotoar.png";

const D_BASE = {
  // baseline civic-survey (current)
  id: "01", name: "Civic Survey · teal",
  tag: "BASELINE",
  bg: "#f1ead9", fg: "#171311", mute: "#6e655c", paperHi: "#f7f1e2",
  accent: "#0d4f4a", accentInk: "#03241f", accentTint: "#cfddd9",
  sev: "#7d2c25", sevTint: "#ecd0cb",
  hivis: "#f3c100",
  fDisplay: "'Plus Jakarta Sans', sans-serif",
  fUtil: "'IBM Plex Mono', monospace",
  fCat: "'Plus Jakarta Sans', sans-serif",
  catWeight: 800, catCase: "lowercase", catLetter: -1.0,
  catText: "parkir liar.",
  rule: 1.5,
  blurb: "Cream paper, sharp rules, lowercase display headline. Serious but warm — civic field tool.",
};

const D_WAY = {
  id: "02", name: "Wayfinding · TransJakarta",
  tag: "PUBLIC SIGNAGE",
  bg: "#f4f1ea", fg: "#0a1430", mute: "#5b6480", paperHi: "#fbfaf6",
  accent: "#1b3a8a", accentInk: "#06143a", accentTint: "#dee5f7",
  sev: "#b03a1c", sevTint: "#f1d5cb",
  hivis: "#f6c80a",
  fDisplay: "'Barlow Condensed', sans-serif",
  fUtil: "'Barlow Condensed', sans-serif",
  fCat: "'Barlow Condensed', sans-serif",
  catWeight: 700, catCase: "uppercase", catLetter: 0.5,
  catText: "PARKIR LIAR",
  rule: 2,
  blurb: "Reads like a TransJakarta sign. Condensed type, big block colors, signage-yellow hi-vis. Belongs on the street.",
};

const D_BATIK = {
  id: "03", name: "Batik · indigo & sogan",
  tag: "INDONESIAN IDENTITY",
  bg: "#ece4d2", fg: "#1a1410", mute: "#7b6c52", paperHi: "#f4ecd9",
  accent: "#1c2a52", accentInk: "#0a1230", accentTint: "#d6dae9",
  sev: "#8a5a2b", sevTint: "#e8d6b8",
  hivis: "#d6a83a",
  fDisplay: "'Plus Jakarta Sans', sans-serif",
  fUtil: "'IBM Plex Mono', monospace",
  fCat: "'Bricolage Grotesque', sans-serif",
  catWeight: 700, catCase: "lowercase", catLetter: -0.7,
  catText: "parkir liar.",
  rule: 1.4,
  pattern: true,
  blurb: "Indigo (biru tarum) + sogan brown, subtle parang-stripe in negative space. Indonesian cultural register without being on-the-nose batik wallpaper.",
};

const D_DARK = {
  id: "04", name: "Dark Field Tool",
  tag: "NIGHT SHIFT",
  bg: "#0e0d0a", fg: "#f3ede0", mute: "#8a8479", paperHi: "#1a1815",
  accent: "#46d3d0", accentInk: "#0a2826", accentTint: "#173432",
  sev: "#e87a4f", sevTint: "#3a1f15",
  hivis: "#f3c100",
  fDisplay: "'Plus Jakarta Sans', sans-serif",
  fUtil: "'Geist Mono', monospace",
  fCat: "'Plus Jakarta Sans', sans-serif",
  catWeight: 800, catCase: "lowercase", catLetter: -1.0,
  catText: "parkir liar.",
  rule: 1.4,
  dark: true,
  blurb: "For night walking + battery savings. Hi-vis amber + cyan bbox, OLED-black panels. Closest to a construction / inspection device.",
};

const D_PRESS = {
  id: "05", name: "Newsprint · editorial",
  tag: "CIVIC GRAVITAS",
  bg: "#f8f5ec", fg: "#0c0a08", mute: "#6b6356", paperHi: "#fdfaf2",
  accent: "#6a221d", accentInk: "#2b0c08", accentTint: "#ead0c8",
  sev: "#6a221d", sevTint: "#ead0c8",
  hivis: "#cf9614",
  fDisplay: "'Plus Jakarta Sans', sans-serif",
  fUtil: "'Plus Jakarta Sans', sans-serif",
  fUtilWeight: 700,
  fCat: "'Instrument Serif', serif",
  catWeight: 400, catCase: "lowercase", catLetter: -0.5, catItalic: true,
  catText: "parkir liar.",
  rule: 1,
  ruleDouble: true,
  noMono: true,
  blurb: "Editorial italic serif for the violation name. Reads as op-ed / civic journalism — frames each audit as a small public document.",
};

const D_STENCIL = {
  id: "06", name: "Public Works · stencil",
  tag: "INFRASTRUCTURE",
  bg: "#fff7d6", fg: "#0c0c0c", mute: "#5a5a3a", paperHi: "#fffce8",
  accent: "#0c0c0c", accentInk: "#0c0c0c", accentTint: "#f0e6a8",
  sev: "#a8170c", sevTint: "#f0d6cf",
  hivis: "#0c0c0c",
  fDisplay: "'Plus Jakarta Sans', sans-serif",
  fUtil: "'Anton', sans-serif",
  fUtilWeight: 400, fUtilLetter: 2.6,
  fCat: "'Anton', sans-serif",
  catWeight: 400, catCase: "uppercase", catLetter: 1,
  catText: "PARKIR LIAR",
  rule: 2,
  stripes: true,
  blurb: "Caution-yellow + stencil display. Overt infrastructure semiotics — like the back of a public-works truck. Loud; would need restraint elsewhere.",
};

const ALL_DIRECTIONS = [D_BASE, D_WAY, D_BATIK, D_DARK, D_PRESS, D_STENCIL];

// =================== Bbox helper (reused, parameterized) ===================
const CarBboxD = ({ h, color }) => {
  const scale = 290 / 465;
  const displayH = 559 * scale;
  const overflow = displayH - h;
  const yOffset = -overflow * 0.22;
  const x1 = 55 * scale, x2 = 445 * scale;
  const y1 = 5 * scale + yOffset, y2 = 285 * scale + yOffset;
  const x = x1, y = Math.max(2, y1), w = x2 - x1, hh = y2 - y;
  const t = 10;
  const ticks = [
    [x, y, x+t, y], [x, y, x, y+t],
    [x+w, y, x+w-t, y], [x+w, y, x+w, y+t],
    [x, y+hh, x+t, y+hh], [x, y+hh, x, y+hh-t],
    [x+w, y+hh, x+w-t, y+hh], [x+w, y+hh, x+w, y+hh-t],
  ];
  return (
    <g>
      <rect x={x} y={y} width={w} height={hh} fill="none" stroke={color} strokeWidth="1.4" strokeDasharray="3 4"/>
      {ticks.map(([a,b,c,d], i) => <line key={i} x1={a} y1={b} x2={c} y2={d} stroke={color} strokeWidth="2.6" strokeLinecap="round"/>)}
    </g>
  );
};

// =================== DirectionTile ===================
const DirectionTile = ({ d }) => {
  const W = 290, H = 580, photoH = 180;
  const utilStyle = {
    fontFamily: d.fUtil,
    fontWeight: d.fUtilWeight ?? 600,
    fontSize: 9.5,
    letterSpacing: d.fUtilLetter ?? 1.5,
    color: d.mute,
    textTransform: "uppercase",
  };
  const catStyle = {
    fontFamily: d.fCat,
    fontWeight: d.catWeight,
    fontStyle: d.catItalic ? "italic" : "normal",
    fontSize: d.catCase === "uppercase" ? 32 : 36,
    lineHeight: 0.95,
    letterSpacing: d.catLetter,
    textTransform: d.catCase,
    color: d.fg,
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
      {/* meta */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", color: "#a39a8c", fontFamily: "'JetBrains Mono', monospace", fontSize: 10, letterSpacing: 1.4, textTransform: "uppercase" }}>
        <span style={{ color: "#f1ead9" }}>{d.id} · {d.name}</span>
        <span>{d.tag}</span>
      </div>

      {/* phone */}
      <div style={{ width: W, height: H, background: "#0a0a08", padding: 5, boxShadow: "0 16px 30px -16px rgba(0,0,0,0.7)" }}>
        <div style={{ position: "relative", width: W - 10, height: H - 10, background: d.bg, color: d.fg, overflow: "hidden" }}>
          {/* photo */}
          <div style={{ position: "absolute", top: 0, left: 0, right: 0, height: photoH, background: "#000", overflow: "hidden" }}>
            <img src={PHOTO_} alt="" style={{ position: "absolute", inset: 0, width: "100%", height: "100%", objectFit: "cover", objectPosition: "center 22%" }}/>
            <svg width="100%" height="100%" viewBox={`0 0 ${W-10} ${photoH}`} preserveAspectRatio="none" style={{ position: "absolute", inset: 0 }}>
              <CarBboxD h={photoH} color={d.hivis}/>
            </svg>
            {/* photo chrome */}
            <div style={{ position: "absolute", top: 8, left: 8, padding: "3px 6px", background: "rgba(0,0,0,0.55)", color: d.hivis, fontFamily: d.fUtil, fontSize: 9, fontWeight: 700, letterSpacing: 1, border: `1px solid ${d.hivis}` }}>
              GEMMA 4 · 0.8s
            </div>
            <div style={{ position: "absolute", bottom: 6, left: 8, fontFamily: d.fUtil, fontSize: 9, color: "#fff", letterSpacing: 0.6, opacity: 0.9 }}>
              JL. KEBON SIRIH
            </div>
          </div>

          {/* optional batik pattern strip just below photo */}
          {d.pattern && (
            <svg width="100%" height="14" viewBox="0 0 280 14" style={{ position: "absolute", top: photoH, left: 0 }}>
              {Array.from({length: 14}).map((_, i) => (
                <g key={i} transform={`translate(${i*22}, 0)`}>
                  <path d="M0 7 Q 5 0 11 7 T 22 7" stroke={d.accent} strokeWidth="1.4" fill="none"/>
                </g>
              ))}
            </svg>
          )}

          {/* sheet */}
          <div style={{
            position: "absolute", top: photoH + (d.pattern ? 14 : 0), left: 0, right: 0, bottom: 0,
            background: d.bg, padding: "12px 14px",
            borderTop: `${d.rule * 1.5}px solid ${d.fg}`,
            display: "flex", flexDirection: "column",
            ...(d.stripes ? { backgroundImage: `repeating-linear-gradient(45deg, ${d.bg} 0 14px, ${d.paperHi} 14px 16px)` } : {}),
          }}>
            {d.ruleDouble && <div style={{ position: "absolute", top: 5, left: 0, right: 0, height: 1, background: d.fg }}/>}
            {/* audit code row */}
            <div style={{ display: "flex", justifyContent: "space-between", ...utilStyle, marginBottom: 8 }}>
              <span>AUDIT · PJ-024-0247</span>
              <span>09:42 WIB</span>
            </div>

            {/* category label */}
            <div style={{ ...utilStyle }}>
              <span style={{ color: d.fg, fontWeight: 700 }}>§01</span> &nbsp;KATEGORI
            </div>

            {/* category headline + severity */}
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-end", gap: 8, marginTop: 2 }}>
              <div style={catStyle}>{d.catText}</div>
              {/* sev stamp */}
              <div style={{ display: "inline-flex", border: `1.4px solid ${d.sev}`, transform: "rotate(-2deg)", fontFamily: d.fUtil, fontSize: 9.5, fontWeight: d.fUtilWeight ?? 700, letterSpacing: d.fUtilLetter ?? 1.2, textTransform: "uppercase", background: d.sevTint, color: d.sev, flexShrink: 0 }}>
                <span style={{ padding: "2px 5px", background: d.sev, color: d.sevTint, borderRight: `1.4px solid ${d.sev}` }}>03</span>
                <span style={{ padding: "2px 7px" }}>tinggi</span>
              </div>
            </div>

            {/* confidence meter */}
            <div style={{ marginTop: 12, display: "flex", flexDirection: "column", gap: 4 }}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
                <span style={utilStyle}>KEYAKINAN · 5/5</span>
                <span style={{ fontFamily: d.fDisplay, fontWeight: 700, fontSize: 11.5, color: d.fg }}>sangat yakin</span>
              </div>
              <div style={{ display: "flex", gap: 2 }}>
                {[1,2,3,4,5].map(i => <div key={i} style={{ flex: 1, height: 10, background: d.accent, border: `1.2px solid ${d.accent}` }}/>)}
              </div>
            </div>

            {/* rationale */}
            <div style={{ marginTop: 10, paddingTop: 8, borderTop: `1.2px solid ${d.fg}` }}>
              <div style={{ fontFamily: d.fDisplay, fontSize: 11.5, lineHeight: 1.4, color: d.fg, fontWeight: 500 }}>
                Trotoar tersumbat penuh oleh mobil parkir. Pejalan harus turun ke jalan raya.
              </div>
            </div>

            {/* chips */}
            <div style={{ marginTop: 10, display: "flex", gap: 4, flexWrap: "wrap" }}>
              {["halangan permanen", "lainnya"].map(t => (
                <div key={t} style={{ padding: "4px 7px", background: d.paperHi, border: `1.2px solid ${d.fg}`, fontFamily: d.fDisplay, fontSize: 10.5, fontWeight: 600, color: d.fg }}>{t}</div>
              ))}
            </div>

            <div style={{ flex: 1 }}/>

            {/* button */}
            <div style={{ display: "flex", gap: 6, marginTop: 8 }}>
              <div style={{ width: 36, height: 36, background: d.paperHi, border: `1.4px solid ${d.fg}`, display: "flex", alignItems: "center", justifyContent: "center", color: d.fg }}>
                <svg width="16" height="16" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round"><rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0 M12 18v3 M8 21h8"/></svg>
              </div>
              <div style={{ flex: 1, height: 36, background: d.fg, color: d.bg, display: "flex", alignItems: "center", justifyContent: "center", fontFamily: d.fDisplay, fontWeight: 700, fontSize: 12 }}>
                Lanjutkan →
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* swatches */}
      <div style={{ display: "flex", gap: 4, marginTop: 2 }}>
        {[d.bg, d.fg, d.accent, d.sev, d.hivis].map((c, i) => (
          <div key={i} style={{ width: 24, height: 24, background: c, border: "1px solid #2a2520" }}/>
        ))}
      </div>

      {/* blurb */}
      <div style={{ width: W, fontSize: 12, color: "#cfc6b0", lineHeight: 1.45, fontFamily: "'Plus Jakarta Sans', sans-serif" }}>
        {d.blurb}
      </div>
    </div>
  );
};

const DirectionsGrid = () => (
  <div style={{ padding: "26px 28px", color: "#f1ead9", fontFamily: "'Plus Jakarta Sans', sans-serif" }}>
    <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, letterSpacing: 2, color: "#a39a8c", textTransform: "uppercase" }}>visual directions · 6 takes</div>
    <div style={{ fontWeight: 800, fontSize: 32, lineHeight: 1, letterSpacing: -0.7, marginTop: 6 }}>
      same moment, six different worlds.
    </div>
    <div style={{ marginTop: 8, fontSize: 13.5, color: "#cfc6b0", maxWidth: 920, lineHeight: 1.5 }}>
      Each tile renders the <i>same</i> high-confidence result so you can read the personality directly. Type, color, scale of headline, severity stamp, even the bbox color all shift together — these are full visual systems, not isolated swaps.
    </div>
    <div style={{ marginTop: 22, display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 26 }}>
      {ALL_DIRECTIONS.map(d => <DirectionTile key={d.id} d={d}/>)}
    </div>
  </div>
);

window.DirectionsGrid = DirectionsGrid;
window.ALL_DIRECTIONS = ALL_DIRECTIONS;

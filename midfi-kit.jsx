// Pejalan — mid-fi flow screens.
// More civic-survey, less "claude-y": cream paper, sharp rules, heavy display type,
// numbered fields, JKT identity via Plus Jakarta Sans, single accent per severity.

// =================== TOKENS ===================
// Committed direction: Civic Survey layout + Batik palette (biru tarum / sogan)
// + Plus Jakarta Sans body + Barlow Semi Condensed utility (wayfinding register).
const M = {
  paper:    "#ece4d2",   // warm cream w/ slight amber cast
  paperHi:  "#f4ecd9",
  paperLo:  "#dcd2bb",
  ink:      "#1a1410",
  inkSoft:  "#322a23",
  rule:     "#1a1410",
  mute:     "#7b6c52",
  muteLo:   "#b5a98a",

  // primary accent — biru tarum (indigo) replaces the old teal
  teal:     "#1c2a52",
  tealInk:  "#0a1230",
  tealTint: "#d6dae9",

  // severity tinggi — deep marun (red reserved for highest tier only)
  ox:       "#7a1f17",
  oxTint:   "#ecd0cb",

  // severity sedang — sogan (Indonesian batik brown)
  amber:    "#8a5a2b",
  amberTint:"#e8d6b8",

  // severity rendah — muted olive (calm, low-alarm)
  olive:    "#5e6b3a",
  oliveTint:"#dee0c6",

  hivis:    "#f3c100",

  fDisplay: "'Plus Jakarta Sans', system-ui, sans-serif",
  fMono:    "'Barlow Semi Condensed', system-ui, sans-serif", // signage / wayfinding register
};

const PHOTO = "assets/mobil-trotoar.png";

// =================== ICONS ===================
const Ic = ({ n, s = 22, c = "currentColor", w = 1.8 }) => {
  const p = { stroke: c, strokeWidth: w, fill: "none", strokeLinecap: "round", strokeLinejoin: "round" };
  switch (n) {
    case "car":     return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M2 16v-4l2-1 2-4h11l3 4 2 1v4 M2 16h20 M2 16v2.5 M22 16v2.5" {...p}/><circle cx="7" cy="17" r="2.2" {...p}/><circle cx="17" cy="17" r="2.2" {...p}/></svg>;
    case "crack":   return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M3 19l3-5 2 2 3-6 3 4 2-3 5 2" {...p}/></svg>;
    case "pole":    return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M12 3v18 M9 5l3-2 3 2 M5 21h14" {...p}/></svg>;
    case "tiles":   return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M3 8h6V3 M9 8h6V3 M15 8h6V3 M3 14h6V8 M9 14h6V8 M15 14h6V8 M3 20h6v-6 M9 20h6v-6 M15 20h6v-6" {...p}/></svg>;
    case "noside":  return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M3 18h18 M5 18l4-9 M19 18l-4-9 M9 9h6" {...p}/><path d="M4 22l16-20" {...p}/></svg>;
    case "drain":   return <svg width={s} height={s} viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" {...p}/><path d="M5 9h14 M5 12h14 M5 15h14" {...p}/></svg>;
    case "mic":     return <svg width={s} height={s} viewBox="0 0 24 24"><rect x="9" y="3" width="6" height="11" rx="3" {...p}/><path d="M5 11a7 7 0 0 0 14 0 M12 18v3 M8 21h8" {...p}/></svg>;
    case "check":   return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M4 13l5 5 11-12" {...p}/></svg>;
    case "x":       return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M5 5l14 14 M19 5L5 19" {...p}/></svg>;
    case "arr":     return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M5 12h14 M13 6l6 6-6 6" {...p}/></svg>;
    case "chev":    return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M9 6l6 6-6 6" {...p}/></svg>;
    case "back":    return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M19 12H5 M11 6l-6 6 6 6" {...p}/></svg>;
    case "shutter": return <svg width={s} height={s} viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" {...p}/><circle cx="12" cy="12" r="6" stroke={c} strokeWidth={w*1.6} fill="none"/></svg>;
    case "stop":    return <svg width={s} height={s} viewBox="0 0 24 24"><rect x="6" y="6" width="12" height="12" rx="1" fill={c}/></svg>;
    case "info":    return <svg width={s} height={s} viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" {...p}/><path d="M12 11v6" {...p}/><circle cx="12" cy="8" r="0.6" fill={c} stroke="none"/></svg>;
    case "loc":     return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M12 22s7-7 7-13a7 7 0 1 0-14 0c0 6 7 13 7 13z" {...p}/><circle cx="12" cy="9" r="2.5" {...p}/></svg>;
    case "edit":    return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M4 20l4-1 11-11-3-3L5 16l-1 4z M14 6l3 3" {...p}/></svg>;
    case "grid":    return <svg width={s} height={s} viewBox="0 0 24 24"><path d="M3 9h18 M3 15h18 M9 3v18 M15 3v18" {...p} strokeWidth={w*0.75}/></svg>;
    default: return null;
  }
};

// =================== PRIMITIVES ===================
// Form-style numbered field label
const FieldLabel = ({ n, children, color = M.mute }) => (
  <div style={{ display: "flex", alignItems: "baseline", gap: 8, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.6, color, textTransform: "uppercase" }}>
    {n && <span style={{ color: M.ink, fontWeight: 600 }}>§{n}</span>}
    <span>{children}</span>
  </div>
);

// Stamp-like severity badge — rectangular, slightly imperfect
const SevStamp = ({ level, rot = -1.5 }) => {
  const map = {
    rendah: { c: M.olive, t: M.oliveTint, code: "01" },
    sedang: { c: M.amber, t: M.amberTint, code: "02" },
    tinggi: { c: M.ox,    t: M.oxTint,    code: "03" },
  };
  const x = map[level];
  return (
    <div style={{
      display: "inline-flex", alignItems: "stretch", border: `1.6px solid ${x.c}`,
      transform: `rotate(${rot}deg)`,
      fontFamily: M.fMono, fontSize: 11, fontWeight: 700, letterSpacing: 1.4,
      background: x.t, color: x.c, textTransform: "uppercase",
    }}>
      <div style={{ padding: "3px 8px 3px 9px", borderRight: `1.6px solid ${x.c}`, background: x.c, color: x.t }}>{x.code}</div>
      <div style={{ padding: "3px 10px 3px 9px" }}>{level}</div>
    </div>
  );
};

// 5-step confidence meter, blocky, with code label on left
const ConfBlocks = ({ level, label, c = M.teal }) => (
  <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
      <span style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>
        keyakinan · {level}/5
      </span>
      <span style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 13, color: M.ink, whiteSpace: "nowrap" }}>{label}</span>
    </div>
    <div style={{ display: "flex", gap: 3 }}>
      {[1,2,3,4,5].map(i => (
        <div key={i} style={{
          flex: 1, height: 14,
          background: i <= level ? c : "transparent",
          border: `1.4px solid ${i <= level ? c : M.muteLo}`,
        }}/>
      ))}
    </div>
  </div>
);

// Big sharp primary button (no rounded)
const Btn = ({ tone = "ink", children, icon, h = 52, fill = true }) => {
  const bg = fill ? (tone === "teal" ? M.teal : tone === "ox" ? M.ox : M.ink) : M.paperHi;
  const fg = fill ? M.paperHi : M.ink;
  return (
    <button style={{
      width: "100%", height: h, border: fill ? "none" : `1.6px solid ${M.ink}`,
      background: bg, color: fg, cursor: "pointer",
      fontFamily: M.fDisplay, fontSize: 16, fontWeight: 700, letterSpacing: 0.2,
      display: "flex", alignItems: "center", justifyContent: "center", gap: 10,
    }}>
      {children}
      {icon && <Ic n={icon} s={20} w={2.2}/>}
    </button>
  );
};

// Square icon button
const SqBtn = ({ icon, size = 52, accent }) => (
  <button style={{
    width: size, height: size, flexShrink: 0,
    background: M.paperHi, color: M.ink, border: `1.6px solid ${M.ink}`,
    cursor: "pointer", display: "inline-flex", alignItems: "center", justifyContent: "center",
    position: "relative",
  }}>
    <Ic n={icon} s={22} w={2}/>
    {accent && <span style={{ position: "absolute", top: -3, right: -3, width: 8, height: 8, background: accent, border: `1.4px solid ${M.ink}` }}/>}
  </button>
);

// =================== PHONE FRAME ===================
const Phone = ({ w = 390, h = 844, children, label, sub }) => (
  <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 12 }}>
    <div style={{ textAlign: "left", width: w, paddingLeft: 4 }}>
      <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 2.4, color: M.muteLo, textTransform: "uppercase" }}>{label}</div>
      <div style={{ fontFamily: M.fDisplay, fontWeight: 600, fontSize: 16, color: M.paper, marginTop: 2 }}>{sub}</div>
    </div>
    <div style={{ position: "relative", width: w + 14, height: h + 14, background: "#0a0a08", padding: 7, boxShadow: "0 25px 40px -20px rgba(0,0,0,0.7)" }}>
      <div style={{ position: "relative", width: w, height: h, background: "#000", overflow: "hidden" }}>
        {/* status bar */}
        <div style={{
          position: "absolute", top: 0, left: 0, right: 0, height: 28, zIndex: 50,
          display: "flex", justifyContent: "space-between", alignItems: "center", padding: "0 18px",
          fontFamily: M.fMono, fontSize: 11, color: "#fff", fontWeight: 600,
        }}>
          <span>09:42</span>
          <span style={{ fontSize: 10, letterSpacing: 1, opacity: 0.85 }}>4G · 88%</span>
        </div>
        {children}
      </div>
    </div>
  </div>
);

// =================== SHARED VIEWFINDER PHOTO ===================
// bbox now tuned to actually wrap the car given the photo crop
const CarPhoto = ({ h = 360, showBbox = true, bboxLabel = "mobil parkir · 0.0 m sisa", scrim = true, blur = 0, dim = 0 }) => (
  <div style={{ position: "absolute", top: 0, left: 0, right: 0, height: h, overflow: "hidden", background: "#000" }}>
    <img src={PHOTO} alt="" style={{
      position: "absolute", inset: 0, width: "100%", height: "100%",
      objectFit: "cover", objectPosition: "center 22%",
      filter: `blur(${blur}px) brightness(${1 - dim})`,
    }}/>
    {scrim && (
      <div style={{
        position: "absolute", top: 0, left: 0, right: 0, height: 70,
        background: "linear-gradient(180deg, rgba(0,0,0,0.55), rgba(0,0,0,0))", pointerEvents: "none",
      }}/>
    )}
    {showBbox && (
      <svg width="100%" height="100%" viewBox={`0 0 390 ${h}`} preserveAspectRatio="none"
        style={{ position: "absolute", inset: 0, pointerEvents: "none" }}>
        {/* Bbox sized to actually wrap the car in the cropped image.
            Photo 465x559, displayed objectFit:cover into 390xh, objectPosition center 22%.
            Computed: scale=390/465=0.839, display height=469, vertical offset = (h-469)*0.22
            Car bounds in source (approx): x:55..445, y:10..280
            Mapped → x:46..373, y:(scaled with vertical offset) */}
        <CarBbox h={h} label={bboxLabel}/>
      </svg>
    )}
  </div>
);

// Compute car bbox properly from photo geometry
const CarBbox = ({ h, label, color = M.hivis }) => {
  const scale = 390 / 465;             // 0.839
  const displayH = 559 * scale;        // ≈ 469
  const overflow = displayH - h;       // total vertical overflow (when h < 469)
  const yOffset = -overflow * 0.22;    // we set objectPosition "center 22%"

  // car source-bounds (approx, eyeballed): x:55..445, y:5..285
  const x1 = 55 * scale;               // ≈ 46
  const x2 = 445 * scale;              // ≈ 373
  const y1 = 5 * scale + yOffset;
  const y2 = 285 * scale + yOffset;
  const x = x1, y = Math.max(2, y1), w = x2 - x1, hh = y2 - y;

  const t = 14;
  const ticks = [
    [x, y, x+t, y], [x, y, x, y+t],
    [x+w, y, x+w-t, y], [x+w, y, x+w, y+t],
    [x, y+hh, x+t, y+hh], [x, y+hh, x, y+hh-t],
    [x+w, y+hh, x+w-t, y+hh], [x+w, y+hh, x+w, y+hh-t],
  ];
  return (
    <g>
      <rect x={x} y={y} width={w} height={hh} fill="none" stroke={color} strokeWidth="1.6" strokeDasharray="3 5" opacity="0.95"/>
      {ticks.map(([a,b,c,d], i) => <line key={i} x1={a} y1={b} x2={c} y2={d} stroke={color} strokeWidth="3.5" strokeLinecap="round"/>)}
      {label && (
        <g transform={`translate(${x}, ${y+hh+8})`}>
          <rect x="0" y="0" width={label.length * 6.6 + 14} height="20" fill={color}/>
          <text x="7" y="14" fontFamily={M.fMono} fontSize="11" fontWeight="700" fill={M.ink} letterSpacing="0.4">{label}</text>
        </g>
      )}
    </g>
  );
};

// Top-of-photo overlay chips (gemma marker, close X, geo strip)
const PhotoChrome = ({ frameLabel = "FOTO TERSIMPAN", showAnalyze = true, customMarker }) => (
  <>
    <div style={{
      position: "absolute", top: 40, left: 14, zIndex: 5,
      display: "inline-flex", alignItems: "center", gap: 6,
      padding: "5px 9px", background: "rgba(0,0,0,0.55)", color: M.hivis,
      fontFamily: M.fMono, fontSize: 10, letterSpacing: 1, fontWeight: 700, border: `1px solid ${M.hivis}`,
    }}>
      {customMarker ? customMarker : (showAnalyze ? <><span style={{ width: 6, height: 6, background: M.hivis, boxShadow: `0 0 6px ${M.hivis}` }}/> ANALISIS · 0.8s · GEMMA 4</> : <>BIDIK</>)}
    </div>
    <button style={{
      position: "absolute", top: 36, right: 12, zIndex: 5,
      width: 36, height: 36, background: "rgba(0,0,0,0.55)", color: "#fff",
      border: "none", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center",
    }}>
      <Ic n="x" s={18} w={2}/>
    </button>
    <div style={{ position: "absolute", bottom: 10, left: 14, zIndex: 5, fontFamily: M.fMono, fontSize: 10, color: "#fff", lineHeight: 1.4, opacity: 0.9 }}>
      <div>JL. KEBON SIRIH</div>
      <div style={{ opacity: 0.7 }}>−6.1854°, 106.8327° · ±4m</div>
    </div>
    <div style={{ position: "absolute", bottom: 10, right: 14, zIndex: 5, fontFamily: M.fMono, fontSize: 10, color: "#fff", opacity: 0.6, letterSpacing: 0.8 }}>
      {frameLabel}
    </div>
  </>
);

// Sheet container — sharp top edge, no rounded
const Sheet = ({ top, accent = M.ink, children }) => (
  <div style={{
    position: "absolute", left: 0, right: 0, top, bottom: 0,
    background: M.paper, color: M.ink,
    borderTop: `3px solid ${accent}`,
    boxShadow: "0 -12px 24px -8px rgba(0,0,0,0.4)",
    padding: "14px 18px 14px",
    display: "flex", flexDirection: "column",
    overflow: "hidden",
  }}>
    {/* form-code header strip */}
    <div style={{ position: "absolute", top: -3, left: 18, height: 3, background: accent, width: 56 }}/>
    {children}
  </div>
);

// Form-code row: small audit identifier
const AuditCode = ({ code = "PJ-024-0247", time = "09:42 WIB" }) => (
  <div style={{ display: "flex", justifyContent: "space-between", fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.6, color: M.mute, textTransform: "uppercase", marginBottom: 12 }}>
    <span>AUDIT · {code}</span>
    <span>{time}</span>
  </div>
);

window.M = M; window.Ic = Ic; window.FieldLabel = FieldLabel; window.SevStamp = SevStamp;
window.ConfBlocks = ConfBlocks; window.Btn = Btn; window.SqBtn = SqBtn; window.Phone = Phone;
window.CarPhoto = CarPhoto; window.CarBbox = CarBbox; window.PhotoChrome = PhotoChrome;
window.Sheet = Sheet; window.AuditCode = AuditCode;

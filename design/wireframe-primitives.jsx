// Sketchy wireframe primitives — hand-drawn feel, b&w with sparse color.

const ink = "#1a1a1a";
const paper = "#fbfaf6";
const muted = "#9a9590";
const hatch = "#d9d5cc";

// A wobbly rectangle drawn as an SVG path so it looks hand-drawn
const WobbleBox = ({ w, h, fill = "transparent", stroke = ink, sw = 1.6, rx = 6, style, children }) => {
  const j = (n) => n + (Math.random() - 0.5) * 0.9; // small jitter
  // four corners with slight wobble
  const x0 = j(1), y0 = j(1);
  const x1 = j(w - 1), y1 = j(1);
  const x2 = j(w - 1), y2 = j(h - 1);
  const x3 = j(1), y3 = j(h - 1);
  const d = `M ${x0} ${y0 + rx}
    Q ${x0} ${y0}, ${x0 + rx} ${y0}
    L ${x1 - rx} ${y1}
    Q ${x1} ${y1}, ${x1} ${y1 + rx}
    L ${x2} ${y2 - rx}
    Q ${x2} ${y2}, ${x2 - rx} ${y2}
    L ${x3 + rx} ${y3}
    Q ${x3} ${y3}, ${x3} ${y3 - rx}
    Z`;
  return (
    <svg width={w} height={h} style={{ display: "block", ...style }}>
      <path d={d} fill={fill} stroke={stroke} strokeWidth={sw} strokeLinejoin="round" strokeLinecap="round" />
      {children}
    </svg>
  );
};

// Hatched / striped placeholder for an image
const HatchedImage = ({ w, h, label = "FOTO", angle = -28, color = ink, dense = false }) => {
  const step = dense ? 6 : 10;
  const lines = [];
  for (let i = -h; i < w + h; i += step) {
    lines.push(<line key={i} x1={i} y1={0} x2={i + h} y2={h} stroke={hatch} strokeWidth={1} />);
  }
  return (
    <div style={{ position: "relative", width: w, height: h }}>
      <svg width={w} height={h} style={{ position: "absolute", inset: 0 }}>
        <rect x="0.5" y="0.5" width={w - 1} height={h - 1} fill="#efece4" stroke={ink} strokeWidth="1.4" />
        <g style={{ transform: `rotate(${angle}deg)`, transformOrigin: "center" }}>
          {lines}
        </g>
        <rect x="0.5" y="0.5" width={w - 1} height={h - 1} fill="none" stroke={ink} strokeWidth="1.4" />
      </svg>
      <div style={{
        position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center",
        fontFamily: "'JetBrains Mono', monospace", fontSize: 11, letterSpacing: 1, color: "#6b6760",
        textShadow: "0 0 4px #efece4, 0 0 4px #efece4",
      }}>
        {label}
      </div>
    </div>
  );
};

// Bounding box overlay rendered on top of an image area
const BBox = ({ x, y, w, h, label, color = "#f5d000" }) => (
  <g>
    {/* corner ticks */}
    {[
      [x, y, x + 14, y], [x, y, x, y + 14],
      [x + w, y, x + w - 14, y], [x + w, y, x + w, y + 14],
      [x, y + h, x + 14, y + h], [x, y + h, x, y + h - 14],
      [x + w, y + h, x + w - 14, y + h], [x + w, y + h, x + w, y + h - 14],
    ].map(([x1, y1, x2, y2], i) => (
      <line key={i} x1={x1} y1={y1} x2={x2} y2={y2} stroke={color} strokeWidth="3" strokeLinecap="round" />
    ))}
    <rect x={x} y={y} width={w} height={h} fill="none" stroke={color} strokeWidth="1.5" strokeDasharray="2 3" opacity="0.85" />
    {label && (
      <g>
        <rect x={x} y={y + h + 4} width={label.length * 5.4 + 12} height={16} fill={ink} rx="2" />
        <text x={x + 6} y={y + h + 15} fontFamily="'JetBrains Mono', monospace" fontSize="9" fill={color} letterSpacing="0.4">
          {label}
        </text>
      </g>
    )}
  </g>
);

// Sketchy chip
const Chip = ({ children, emphasized = false, w, h = 36 }) => (
  <div style={{
    position: "relative", height: h, minWidth: w, display: "inline-flex", alignItems: "center", justifyContent: "center",
    padding: "0 12px", gap: 6,
    fontFamily: "'Kalam', cursive", fontSize: emphasized ? 14 : 13, fontWeight: emphasized ? 700 : 400,
  }}>
    <div style={{ position: "absolute", inset: 0 }}>
      <WobbleBox w={w || 110} h={h} sw={emphasized ? 2.2 : 1.3} fill={emphasized ? "#fff7d6" : "transparent"} rx={h / 2} />
    </div>
    <span style={{ position: "relative", color: ink }}>{children}</span>
  </div>
);

// Sketchy primary button
const PrimaryButton = ({ children, w = 280, h = 52, tone = "ink" }) => {
  const bg = tone === "ink" ? ink : "#0d5b5b";
  return (
    <div style={{ position: "relative", width: w, height: h, display: "inline-flex", alignItems: "center", justifyContent: "center" }}>
      <svg width={w} height={h} style={{ position: "absolute", inset: 0 }}>
        <rect x="1" y="3" width={w - 2} height={h - 4} rx="8" fill={bg} stroke={ink} strokeWidth="1.4" />
        <rect x="1" y="3" width={w - 2} height={h - 4} rx="8" fill="none" stroke="#fff" strokeWidth="0.6" strokeDasharray="0" opacity="0.15" />
      </svg>
      <span style={{ position: "relative", color: paper, fontFamily: "'Kalam', cursive", fontSize: 17, fontWeight: 700, letterSpacing: 0.3 }}>
        {children}
      </span>
    </div>
  );
};

// Tiny iconography drawn as sketchy SVG (single-stroke style)
const Icon = ({ name, size = 22, color = ink }) => {
  const s = size;
  const sw = 1.6;
  const common = { stroke: color, strokeWidth: sw, fill: "none", strokeLinecap: "round", strokeLinejoin: "round" };
  switch (name) {
    case "mic":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24">
          <rect x="9" y="3" width="6" height="11" rx="3" {...common} />
          <path d="M5 11a7 7 0 0 0 14 0 M12 18v3 M8 21h8" {...common} />
        </svg>
      );
    case "check":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><path d="M4 12l5 5 11-12" {...common} /></svg>
      );
    case "edit":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><path d="M4 20l4-1 11-11-3-3L5 16l-1 4z M14 6l3 3" {...common} /></svg>
      );
    case "info":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" {...common} /><path d="M12 11v6 M12 7.5v.5" {...common} /></svg>
      );
    case "chev":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><path d="M9 6l6 6-6 6" {...common} /></svg>
      );
    case "car":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24">
          <path d="M3 16v-3l2-5h14l2 5v3 M3 16h18 M3 16v3 M21 16v3" {...common} />
          <circle cx="7" cy="17.5" r="1.6" {...common} /><circle cx="17" cy="17.5" r="1.6" {...common} />
        </svg>
      );
    case "tiles":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24">
          <rect x="3" y="3" width="7" height="7" {...common} />
          <rect x="14" y="3" width="7" height="7" {...common} />
          <rect x="3" y="14" width="7" height="7" {...common} />
          <rect x="14" y="14" width="7" height="7" {...common} />
          <path d="M6 6l1 1 M17 6l1 1 M6 17l1 1 M17 17l1 1" {...common} />
        </svg>
      );
    case "crack":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><path d="M3 18l4-6 3 3 4-7 3 5 4-2" {...common} /></svg>
      );
    case "pole":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><path d="M12 3v18 M9 6h6 M8 21h8" {...common} /></svg>
      );
    case "noside":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><path d="M3 18h18 M5 18l4-8 M19 18l-4-8 M9 10l6 0" {...common} strokeDasharray="3 2" /></svg>
      );
    case "drain":
      return (
        <svg width={s} height={s} viewBox="0 0 24 24"><circle cx="12" cy="12" r="8" {...common} /><path d="M8 9h8 M8 12h8 M8 15h8" {...common} /></svg>
      );
    default: return null;
  }
};

// Severity pill — 3 dots + label, muted palette
const SeverityPill = ({ level }) => {
  const map = { rendah: 1, sedang: 2, tinggi: 3 };
  const tones = { rendah: "#7a8a6a", sedang: "#c08a3e", tinggi: "#9c3a2a" };
  const n = map[level] || 1;
  return (
    <div style={{
      display: "inline-flex", alignItems: "center", gap: 6, padding: "4px 9px",
      fontFamily: "'Kalam', cursive", fontSize: 12, fontWeight: 700, color: tones[level], textTransform: "uppercase", letterSpacing: 0.6,
      position: "relative",
    }}>
      <div style={{ position: "absolute", inset: 0 }}>
        <WobbleBox w={92} h={24} sw={1.3} rx={12} stroke={tones[level]} />
      </div>
      <span style={{ position: "relative", display: "flex", gap: 3 }}>
        {[0, 1, 2].map((i) => (
          <span key={i} style={{
            width: 6, height: 6, borderRadius: "50%",
            background: i < n ? tones[level] : "transparent",
            border: `1px solid ${tones[level]}`,
          }} />
        ))}
      </span>
      <span style={{ position: "relative" }}>{level}</span>
    </div>
  );
};

// Phone frame — sketchy outline
const PhoneFrame = ({ width = 360, height = 760, children }) => (
  <div style={{ position: "relative", width: width + 14, height: height + 14 }}>
    <svg width={width + 14} height={height + 14} style={{ position: "absolute", inset: 0 }}>
      <rect x="3" y="3" width={width + 8} height={height + 8} rx="34" fill={paper} stroke={ink} strokeWidth="2" />
      <rect x="6" y="6" width={width + 2} height={height + 2} rx="30" fill="none" stroke={ink} strokeWidth="0.6" opacity="0.3" />
      {/* notch */}
      <rect x={width / 2 - 30 + 7} y="10" width="60" height="14" rx="7" fill={ink} />
    </svg>
    <div style={{
      position: "absolute", left: 7, top: 7, width, height,
      borderRadius: 30, overflow: "hidden", background: paper,
    }}>
      {children}
    </div>
  </div>
);

Object.assign(window, {
  WobbleBox, HatchedImage, BBox, Chip, PrimaryButton, Icon, SeverityPill, PhoneFrame,
  WF_INK: ink, WF_PAPER: paper, WF_MUTED: muted, WF_HATCH: hatch,
});

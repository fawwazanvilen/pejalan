// Hi-fi components for the Pejalan capture overlay.
// Loaded after hifi-tokens.js — reads window.PJ.

const T = window.PJ;

// ============================================================
// Iconography — single-stroke, civic-tech, slightly rounded
// ============================================================
const Icon = ({ name, size = 22, color = "currentColor", weight = 1.7 }) => {
  const s = size;
  const c = { stroke: color, strokeWidth: weight, fill: "none", strokeLinecap: "round", strokeLinejoin: "round" };
  const f = { fill: color, stroke: "none" };
  switch (name) {
    case "car-side": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <path d="M2 16v-4l2-1 2-4h11l3 4 2 1v4 M2 16h2 M20 16h2 M2 16v2.5 M22 16v2.5" {...c} />
        <circle cx="7" cy="17" r="2.2" {...c} /><circle cx="17" cy="17" r="2.2" {...c} />
      </svg>
    );
    case "crack": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <path d="M3 19l3-5 2 2 3-6 3 4 2-3 5 2" {...c} />
        <path d="M3 21h18" {...c} strokeWidth={weight * 0.7} opacity="0.5" />
      </svg>
    );
    case "pole": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <path d="M12 3v18 M9 5l3-2 3 2 M5 21h14" {...c} />
        <circle cx="12" cy="9" r="1.4" {...c} />
      </svg>
    );
    case "tiles": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <path d="M3 8h6V3 M9 8h6V3 M15 8h6V3 M3 14h6V8 M9 14h6V8 M15 14h6V8 M3 20h6v-6 M9 20h6v-6 M15 20h6v-6" {...c} strokeWidth={weight * 0.85} />
        <circle cx="6" cy="11" r="0.9" {...f} />
        <circle cx="12" cy="11" r="0.9" {...f} />
        <circle cx="18" cy="11" r="0.9" {...f} />
        <circle cx="6" cy="17" r="0.9" {...f} />
        <circle cx="12" cy="17" r="0.9" {...f} />
        <circle cx="18" cy="17" r="0.9" {...f} />
      </svg>
    );
    case "noside": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <path d="M3 18h18 M5 18l4-9 M19 18l-4-9" {...c} />
        <path d="M9 9h6" {...c} strokeDasharray="2.5 2" />
        <path d="M4 22l16-20" {...c} strokeWidth={weight * 0.8} opacity="0.6" />
      </svg>
    );
    case "drain": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="9" {...c} />
        <path d="M5 9h14 M5 12h14 M5 15h14" {...c} />
      </svg>
    );
    case "mic": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <rect x="9" y="3" width="6" height="11" rx="3" {...c} />
        <path d="M5 11a7 7 0 0 0 14 0 M12 18v3 M8 21h8" {...c} />
      </svg>
    );
    case "check": return (
      <svg width={s} height={s} viewBox="0 0 24 24"><path d="M4 13l5 5 11-12" {...c} /></svg>
    );
    case "info": return (
      <svg width={s} height={s} viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" {...c} /><path d="M12 11v6" {...c} /><circle cx="12" cy="8" r="0.6" {...f} /></svg>
    );
    case "chev": return (
      <svg width={s} height={s} viewBox="0 0 24 24"><path d="M9 6l6 6-6 6" {...c} /></svg>
    );
    case "spark": return (
      <svg width={s} height={s} viewBox="0 0 24 24">
        <path d="M12 3l1.7 5.3L19 10l-5.3 1.7L12 17l-1.7-5.3L5 10l5.3-1.7z M19 3v3 M21 5h-3 M5 18v2 M6 19H4" {...c} strokeWidth={weight * 0.9} />
      </svg>
    );
    case "x": return (
      <svg width={s} height={s} viewBox="0 0 24 24"><path d="M5 5l14 14 M19 5L5 19" {...c} /></svg>
    );
    case "flash": return (
      <svg width={s} height={s} viewBox="0 0 24 24"><path d="M13 3L5 13h6l-1 8 8-10h-6l1-8z" {...c} /></svg>
    );
    case "grid": return (
      <svg width={s} height={s} viewBox="0 0 24 24"><path d="M3 9h18 M3 15h18 M9 3v18 M15 3v18" {...c} strokeWidth={weight * 0.7} /></svg>
    );
    default: return null;
  }
};

// ============================================================
// Confidence — 5-step meter (no false-precision %)
// 1=sangat ragu, 2=ragu, 3=cukup yakin, 4=yakin, 5=sangat yakin
// ============================================================
const CONF_LABELS = ["sangat ragu", "ragu", "cukup yakin", "yakin", "sangat yakin"];
const ConfidenceMeter = ({ level, tone = "teal", compact = false }) => {
  // level 1..5
  const color = tone === "amber" ? T.sevMid : T.teal;
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: compact ? 4 : 6 }}>
      <div style={{ display: "flex", alignItems: "baseline", justifyContent: "space-between", gap: 8 }}>
        <span style={{ fontFamily: T.fMono, fontSize: 10.5, letterSpacing: 1.2, color: T.mute, textTransform: "uppercase", whiteSpace: "nowrap" }}>
          keyakinan AI
        </span>
        <span style={{ fontFamily: T.fSans, fontSize: compact ? 13 : 14, fontWeight: 600, color: T.ink, whiteSpace: "nowrap" }}>
          {CONF_LABELS[level - 1]}
        </span>
      </div>
      <div style={{ display: "flex", gap: 4 }}>
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} style={{
            flex: 1, height: compact ? 8 : 10,
            background: i <= level ? color : "transparent",
            border: `1.4px solid ${i <= level ? color : T.rule}`,
            borderRadius: 2,
          }} />
        ))}
      </div>
    </div>
  );
};

// ============================================================
// Severity badge — muted earth palette
// ============================================================
const Severity = ({ level }) => {
  const map = {
    rendah: { fg: T.sevLow, bg: T.sevLowBg, dots: 1 },
    sedang: { fg: T.sevMid, bg: T.sevMidBg, dots: 2 },
    tinggi: { fg: T.sevHigh, bg: T.sevHighBg, dots: 3 },
  };
  const cfg = map[level];
  return (
    <div style={{
      display: "inline-flex", alignItems: "center", gap: 8,
      padding: "4px 10px 4px 8px",
      background: cfg.bg, color: cfg.fg,
      border: `1px solid ${cfg.fg}`,
      borderRadius: 999,
      fontFamily: T.fSans, fontSize: 12, fontWeight: 600,
      textTransform: "uppercase", letterSpacing: 0.6,
    }}>
      <span style={{ display: "inline-flex", gap: 2 }}>
        {[0, 1, 2].map((i) => (
          <span key={i} style={{
            width: 6, height: 6, borderRadius: "50%",
            background: i < cfg.dots ? cfg.fg : "transparent",
            border: `1px solid ${cfg.fg}`,
          }} />
        ))}
      </span>
      <span>{level}</span>
    </div>
  );
};

// ============================================================
// Bounding box SVG overlay (renders into a parent <svg>)
// ============================================================
const BBox = ({ x, y, w, h, label, color = T.hivis, ink = T.hivisInk, anchor = "bottom" }) => {
  const t = 8; // corner-tick length
  const ticks = [
    [x, y, x + t, y], [x, y, x, y + t],
    [x + w, y, x + w - t, y], [x + w, y, x + w, y + t],
    [x, y + h, x + t, y + h], [x, y + h, x, y + h - t],
    [x + w, y + h, x + w - t, y + h], [x + w, y + h, x + w, y + h - t],
  ];
  const labelY = anchor === "bottom" ? y + h + 6 : y - 22;
  const labelX = x;
  const labelW = Math.max(label.length * 6.2 + 16, 80);
  return (
    <g>
      <rect x={x} y={y} width={w} height={h} fill="none" stroke={color} strokeWidth="1.5"
        strokeDasharray="3 4" opacity="0.95" />
      {ticks.map(([x1, y1, x2, y2], i) => (
        <line key={i} x1={x1} y1={y1} x2={x2} y2={y2} stroke={color} strokeWidth="3" strokeLinecap="round" />
      ))}
      {label && (
        <g>
          <rect x={labelX} y={labelY} width={labelW} height={20} fill={color} rx="2" />
          <rect x={labelX + 2} y={labelY + 2} width={labelW - 4} height={16} fill="none" stroke={ink} strokeWidth="0.6" opacity="0.3" rx="1" />
          <text x={labelX + 8} y={labelY + 14} fontFamily={T.fMono} fontSize="11" fontWeight="600" fill={ink} letterSpacing="0.3">
            {label}
          </text>
        </g>
      )}
    </g>
  );
};

// ============================================================
// Category icon block — square with subtle background
// ============================================================
const CategoryIcon = ({ name, size = 48, tone = "teal", active = true }) => {
  const bg = active ? T.tealTint : T.paperLo;
  const fg = active ? T.tealInk : T.mute;
  return (
    <div style={{
      width: size, height: size, borderRadius: 12,
      background: bg, color: fg,
      display: "flex", alignItems: "center", justifyContent: "center",
      border: `1px solid ${active ? "rgba(14,77,74,0.15)" : T.rule}`,
    }}>
      <Icon name={name} size={size * 0.55} color={fg} weight={1.8} />
    </div>
  );
};

// ============================================================
// Primary CTA (teal, full width)
// ============================================================
const PrimaryCTA = ({ children, icon = "check", height = 52 }) => (
  <button style={{
    width: "100%", height, border: "none", cursor: "pointer",
    background: T.teal, color: T.paperHi,
    fontFamily: T.fSans, fontSize: 17, fontWeight: 600, letterSpacing: 0.2,
    borderRadius: 12, display: "flex", alignItems: "center", justifyContent: "center", gap: 8,
    boxShadow: "0 1px 0 rgba(0,0,0,0.04), 0 8px 18px -8px rgba(14,77,74,0.5)",
  }}>
    {children}
    {icon && <Icon name={icon} size={20} color={T.paperHi} weight={2.2} />}
  </button>
);

// ============================================================
// Secondary / outline button (square mic, etc.)
// ============================================================
const IconButton = ({ icon, size = 52, label }) => (
  <button style={{
    height: size, minWidth: size, padding: label ? "0 14px 0 10px" : 0,
    background: T.paperHi, color: T.ink, border: `1.4px solid ${T.ink}`,
    fontFamily: T.fSans, fontSize: 14, fontWeight: 600,
    borderRadius: 12, cursor: "pointer",
    display: "inline-flex", alignItems: "center", justifyContent: "center", gap: 8,
  }}>
    <Icon name={icon} size={22} weight={1.9} />
    {label && <span>{label}</span>}
  </button>
);

// ============================================================
// Correction chip (subtle, used in high-conf path)
// ============================================================
const Chip = ({ icon, label, h = 40 }) => (
  <button style={{
    height: h, padding: "0 14px",
    background: T.paperHi, color: T.ink, border: `1.2px solid ${T.rule}`,
    fontFamily: T.fSans, fontSize: 13.5, fontWeight: 500,
    borderRadius: 999, cursor: "pointer",
    display: "inline-flex", alignItems: "center", gap: 8,
    whiteSpace: "nowrap", flexShrink: 0,
  }}>
    {icon && <Icon name={icon} size={16} color={T.mute} weight={1.8} />}
    <span>{label}</span>
  </button>
);

// ============================================================
// Correction row (low-conf path) — selectable line with bar
// ============================================================
const CorrectionRow = ({ icon, label, share, selected = false }) => (
  <button style={{
    width: "100%", padding: "10px 12px",
    background: selected ? T.tealTint : T.paperHi,
    border: `1.6px solid ${selected ? T.teal : T.rule}`,
    borderRadius: 12, cursor: "pointer",
    display: "flex", alignItems: "center", gap: 12,
    textAlign: "left",
  }}>
    <CategoryIcon name={icon} size={36} active={selected} />
    <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 4 }}>
      <div style={{
        fontFamily: T.fSans, fontSize: 14.5, fontWeight: selected ? 700 : 500, color: T.ink, lineHeight: 1.1,
      }}>{label}</div>
      <div style={{ height: 4, background: T.paperLo, borderRadius: 2, overflow: "hidden" }}>
        <div style={{ width: `${share * 100}%`, height: "100%", background: selected ? T.teal : T.muteHi }} />
      </div>
    </div>
    <div style={{ fontFamily: T.fMono, fontSize: 11, color: selected ? T.tealInk : T.mute, fontWeight: 600, letterSpacing: 0.4 }}>
      {Math.round(share * 100)}
    </div>
  </button>
);

// ============================================================
// Phone frame — Android, status bar, safe areas
// ============================================================
const PhoneFrame = ({ width = 390, height = 844, label, sublabel, children }) => (
  <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 14 }}>
    <div style={{ textAlign: "center" }}>
      <div style={{ fontFamily: T.fMono, fontSize: 11, letterSpacing: 1.4, color: "#d6d1c4", textTransform: "uppercase" }}>{label}</div>
      <div style={{ fontFamily: T.fSans, fontSize: 14, color: "#8a857a", marginTop: 4 }}>{sublabel}</div>
    </div>
    <div style={{
      position: "relative", width: width + 16, height: height + 16,
      background: "#0a0c0c", borderRadius: 48,
      boxShadow: "0 30px 60px -20px rgba(0,0,0,0.7), 0 0 0 1.5px #1c1f1e inset, 0 0 0 6px #18191a",
      padding: 8,
    }}>
      <div style={{ position: "relative", width, height, borderRadius: 40, overflow: "hidden", background: "#000" }}>
        {/* status bar */}
        <div style={{
          position: "absolute", top: 0, left: 0, right: 0, height: 32, zIndex: 5,
          display: "flex", justifyContent: "space-between", alignItems: "center", padding: "0 22px",
          fontFamily: T.fMono, fontSize: 12, color: "#fff", fontWeight: 600,
        }}>
          <span>09:42</span>
          <div style={{ display: "flex", gap: 6, alignItems: "center", fontSize: 10 }}>
            <span>4G</span>
            <svg width="18" height="10" viewBox="0 0 18 10"><rect x="0.5" y="2" width="14" height="6" rx="1" stroke="#fff" fill="none" /><rect x="2" y="3.5" width="9" height="3" fill="#fff" /><rect x="15" y="3.5" width="2" height="3" fill="#fff" /></svg>
          </div>
        </div>
        {/* camera punch hole */}
        <div style={{ position: "absolute", top: 12, left: "50%", transform: "translateX(-50%)", width: 10, height: 10, background: "#0a0a0a", borderRadius: "50%", zIndex: 6 }} />
        {children}
      </div>
    </div>
  </div>
);

Object.assign(window, {
  Icon, ConfidenceMeter, Severity, BBox, CategoryIcon,
  PrimaryCTA, IconButton, Chip, CorrectionRow, PhoneFrame,
});

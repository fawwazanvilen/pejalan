// The two capture-overlay screens, hi-fi.
// Loaded after hifi-tokens.js + hifi-components.jsx.

const TK = window.PJ;
const PHOTO = "assets/mobil-trotoar.png";

// ============================================================
// Viewfinder area — shows captured photo with bbox overlay
// `photoStyle` lets us re-crop the same photo for variant B
// ============================================================
const Viewfinder = ({ height, bbox, photoStyle = {}, captureLabel = "FOTO TERSIMPAN" }) => (
  <div style={{ position: "absolute", top: 0, left: 0, right: 0, height, background: "#000", overflow: "hidden" }}>
    {/* photo */}
    <img src={PHOTO} alt="" style={{
      position: "absolute", inset: 0, width: "100%", height: "100%",
      objectFit: "cover", objectPosition: "center 35%",
      ...photoStyle,
    }} />
    {/* gradient scrim at top + bottom for readability of overlays */}
    <div style={{
      position: "absolute", top: 0, left: 0, right: 0, height: 90,
      background: "linear-gradient(180deg, rgba(0,0,0,0.55), rgba(0,0,0,0))",
      pointerEvents: "none",
    }} />
    <div style={{
      position: "absolute", bottom: 0, left: 0, right: 0, height: 60,
      background: "linear-gradient(180deg, rgba(0,0,0,0), rgba(0,0,0,0.35))",
      pointerEvents: "none",
    }} />

    {/* bbox overlay */}
    <svg width="100%" height="100%" viewBox={`0 0 390 ${height}`} preserveAspectRatio="none"
      style={{ position: "absolute", inset: 0, pointerEvents: "none" }}>
      <BBox {...bbox} />
    </svg>

    {/* top-left analyse chip */}
    <div style={{
      position: "absolute", top: 44, left: 16, zIndex: 4,
      display: "inline-flex", alignItems: "center", gap: 6,
      padding: "5px 9px 5px 8px",
      background: "rgba(14,23,20,0.7)", backdropFilter: "blur(6px)",
      border: `1px solid ${TK.hivis}`, borderRadius: 999,
      color: TK.hivis, fontFamily: TK.fMono, fontSize: 10.5, fontWeight: 600, letterSpacing: 0.8,
    }}>
      <span style={{ width: 6, height: 6, borderRadius: "50%", background: TK.hivis, boxShadow: `0 0 6px ${TK.hivis}` }} />
      ANALISIS · 0.8s · GEMMA 4
    </div>

    {/* top-right close */}
    <button style={{
      position: "absolute", top: 40, right: 14, zIndex: 4,
      width: 36, height: 36, borderRadius: 18,
      background: "rgba(14,23,20,0.55)", backdropFilter: "blur(6px)",
      border: "none", color: "#fff", cursor: "pointer",
      display: "flex", alignItems: "center", justifyContent: "center",
    }}>
      <Icon name="x" size={18} weight={2} />
    </button>

    {/* faint photo metadata strip */}
    <div style={{
      position: "absolute", bottom: 10, left: 14, zIndex: 4,
      fontFamily: TK.fMono, fontSize: 10, color: "rgba(255,255,255,0.78)", letterSpacing: 0.6,
    }}>
      <div>JL. KEBON SIRIH — 09:41</div>
      <div style={{ opacity: 0.75 }}>−6.1854°, 106.8327°  ·  ±4m</div>
    </div>
    <div style={{
      position: "absolute", bottom: 10, right: 14, zIndex: 4,
      fontFamily: TK.fMono, fontSize: 10, color: "rgba(255,255,255,0.6)", letterSpacing: 0.8,
    }}>
      {captureLabel}
    </div>
  </div>
);

// ============================================================
// Sheet container (the bottom overlay) — shared chrome
// ============================================================
const Sheet = ({ top, children, accent }) => (
  <div style={{
    position: "absolute", left: 0, right: 0, top, bottom: 0,
    background: TK.paper,
    borderTopLeftRadius: 24, borderTopRightRadius: 24,
    boxShadow: "0 -16px 30px -10px rgba(0,0,0,0.35)",
    display: "flex", flexDirection: "column",
    paddingTop: 8,
    overflow: "hidden",
  }}>
    {/* top edge accent line (variant-specific color) */}
    {accent && <div style={{
      position: "absolute", top: 0, left: 24, right: 24, height: 3,
      background: accent, borderRadius: 3,
    }} />}
    {/* drag handle */}
    <div style={{ display: "flex", justifyContent: "center", paddingTop: 6, paddingBottom: 10 }}>
      <div style={{ width: 40, height: 4, borderRadius: 2, background: TK.rule }} />
    </div>
    <div style={{ flex: 1, padding: "0 20px 18px", display: "flex", flexDirection: "column", overflow: "hidden" }}>
      {children}
    </div>
  </div>
);

// ============================================================
// Rationale block — quoted line with left rule
// ============================================================
const Rationale = ({ children }) => (
  <div style={{
    position: "relative", padding: "8px 12px 8px 14px",
    background: "rgba(14,77,74,0.05)",
    borderLeft: `3px solid ${TK.teal}`,
    borderRadius: "0 8px 8px 0",
  }}>
    <div style={{
      fontFamily: TK.fSans, fontSize: 14.5, lineHeight: 1.4, color: TK.ink, fontWeight: 500,
    }}>
      <span style={{ color: TK.teal, fontWeight: 700, marginRight: 2 }}>“</span>
      {children}
      <span style={{ color: TK.teal, fontWeight: 700, marginLeft: 2 }}>”</span>
    </div>
  </div>
);

// ============================================================
// VARIANT A — high confidence
// 97% (→ "sangat yakin"), PARKIR_LIAR, TINGGI
// chips: subtle, primary CTA dominates
// ============================================================
const VariantA = () => {
  const viewfinderH = 360;
  return (
    <>
      <Viewfinder
        height={viewfinderH}
        bbox={{
          x: 70, y: 80, w: 240, h: 200,
          label: "mobil parkir · 0.0m sisa lebar trotoar",
          anchor: "bottom",
        }}
      />
      <Sheet top={viewfinderH - 16} accent={TK.teal}>
        {/* HEADER ROW: category + confidence */}
        <div style={{ display: "flex", alignItems: "flex-start", gap: 12, marginTop: 2 }}>
          <CategoryIcon name="car-side" size={48} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontFamily: TK.fMono, fontSize: 10.5, letterSpacing: 1.2, color: TK.mute, textTransform: "uppercase" }}>
              kategori
            </div>
            <div style={{
              fontFamily: TK.fSans, fontSize: 22, fontWeight: 700, color: TK.ink,
              lineHeight: 1.05, letterSpacing: -0.3, marginTop: 2,
            }}>
              Parkir Liar
            </div>
          </div>
          <Severity level="tinggi" />
        </div>

        {/* CONFIDENCE METER */}
        <div style={{ marginTop: 14 }}>
          <ConfidenceMeter level={5} />
        </div>

        {/* RATIONALE */}
        <div style={{ marginTop: 12 }}>
          <Rationale>
            Trotoar tersumbat oleh kendaraan parkir, pejalan kaki harus turun ke jalan raya.
          </Rationale>
        </div>

        {/* CORRECTION CHIPS — subtle */}
        <div style={{ marginTop: 14 }}>
          <div style={{
            fontFamily: TK.fMono, fontSize: 10.5, letterSpacing: 1, color: TK.mute,
            textTransform: "uppercase", marginBottom: 8,
          }}>
            bukan ini?  ganti:
          </div>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <Chip icon="pole" label="halangan permanen" />
            <Chip icon="crack" label="trotoar rusak" />
            <Chip label="lainnya…" />
          </div>
        </div>

        <div style={{ flex: 1 }} />

        {/* FOOTER: mic + primary side-by-side */}
        <div style={{ display: "flex", gap: 10, marginTop: 16 }}>
          <IconButton icon="mic" size={52} />
          <div style={{ flex: 1 }}>
            <PrimaryCTA icon="check">Lanjutkan</PrimaryCTA>
          </div>
        </div>

        {/* TERTIARY */}
        <button style={{
          marginTop: 10, alignSelf: "center", background: "none", border: "none", cursor: "pointer",
          fontFamily: TK.fSans, fontSize: 13.5, fontWeight: 500, color: TK.mute,
          display: "inline-flex", alignItems: "center", gap: 4, padding: "6px 8px",
        }}>
          <Icon name="info" size={14} color={TK.mute} />
          Lihat detail penalaran
          <Icon name="chev" size={14} color={TK.mute} />
        </button>
      </Sheet>
    </>
  );
};

// ============================================================
// VARIANT B — medium confidence
// 64% (→ "cukup yakin"), UBIN_DIFABEL_BERMASALAH, SEDANG
// chips: hero card with selectable correction rows
// ============================================================
const VariantB = () => {
  const viewfinderH = 230; // tighter — sheet needs the room for correction list
  return (
    <>
      <Viewfinder
        height={viewfinderH}
        photoStyle={{ objectPosition: "20% 75%", transform: "scale(1.4)" }}
        bbox={{
          x: 60, y: 80, w: 250, h: 110,
          label: "pola ubin difabel · tidak jelas",
          anchor: "top",
        }}
        captureLabel="FOTO #2 / 47 HARI INI"
      />
      <Sheet top={viewfinderH - 16} accent={TK.sevMid}>
        {/* AGAK RAGU banner */}
        <div style={{
          display: "flex", alignItems: "flex-start", gap: 8,
          padding: "7px 12px 7px 10px",
          background: TK.sevMidBg,
          border: `1px solid ${TK.sevMid}`,
          borderRadius: 10,
        }}>
          <div style={{ flexShrink: 0, marginTop: 1 }}>
            <Icon name="info" size={17} color={TK.sevMid} weight={1.9} />
          </div>
          <div style={{ fontFamily: TK.fSans, fontSize: 12.5, lineHeight: 1.35, color: TK.ink }}>
            <span style={{ fontWeight: 700, color: TK.sevMid }}>Agak ragu.</span>{" "}
            Tolong cek hasil — pilih kategori yang benar di bawah.
          </div>
        </div>

        {/* SUMMARY ROW */}
        <div style={{ display: "flex", alignItems: "flex-start", gap: 10, marginTop: 10 }}>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontFamily: TK.fMono, fontSize: 10, letterSpacing: 1.2, color: TK.mute, textTransform: "uppercase", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
              dugaan AI · kemungkinan tertinggi
            </div>
            <div style={{
              fontFamily: TK.fSans, fontSize: 18, fontWeight: 700, color: TK.ink,
              lineHeight: 1.1, letterSpacing: -0.2, marginTop: 2,
            }}>
              Ubin Difabel Bermasalah
            </div>
          </div>
          <Severity level="sedang" />
        </div>

        {/* CONFIDENCE METER — amber tone */}
        <div style={{ marginTop: 8 }}>
          <ConfidenceMeter level={3} tone="amber" compact />
        </div>

        {/* RATIONALE — compressed */}
        <div style={{ marginTop: 8 }}>
          <Rationale>
            Pola ubin pemandu tidak konsisten — penyandang netra bisa kehilangan jalur.
          </Rationale>
        </div>

        {/* CORRECTION HERO CARD */}
        <div style={{
          marginTop: 10, padding: "8px 10px 6px",
          background: TK.paperHi, border: `1.6px solid ${TK.ink}`,
          borderRadius: 12,
        }}>
          <div style={{
            display: "flex", alignItems: "center", gap: 6,
            fontFamily: TK.fSans, fontSize: 12.5, fontWeight: 700, color: TK.ink,
            marginBottom: 6, padding: "0 2px",
          }}>
            <Icon name="spark" size={14} color={TK.teal} weight={1.8} />
            Apa sebenarnya yang terlihat?
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: 5 }}>
            <CorrectionRow icon="tiles" label="Ubin difabel bermasalah" share={0.64} selected />
            <CorrectionRow icon="crack" label="Trotoar rusak" share={0.21} />
            <CorrectionRow icon="drain" label="Drainase / lubang" share={0.09} />
          </div>
          <button style={{
            marginTop: 4, width: "100%", padding: "6px 0",
            background: "none", border: "none", cursor: "pointer",
            fontFamily: TK.fSans, fontSize: 12.5, fontWeight: 500, color: TK.mute,
            display: "inline-flex", alignItems: "center", justifyContent: "center", gap: 4,
          }}>
            6 kategori lainnya
            <Icon name="chev" size={12} color={TK.mute} />
          </button>
        </div>

        <div style={{ flex: 1, minHeight: 6 }} />

        {/* FOOTER */}
        <div style={{ display: "flex", gap: 10, marginTop: 10 }}>
          <IconButton icon="mic" size={52} />
          <div style={{ flex: 1 }}>
            <PrimaryCTA icon="check">Konfirmasi & Lanjut</PrimaryCTA>
          </div>
        </div>

        <button style={{
          marginTop: 6, alignSelf: "center", background: "none", border: "none", cursor: "pointer",
          fontFamily: TK.fSans, fontSize: 13, fontWeight: 500, color: TK.mute,
          display: "inline-flex", alignItems: "center", gap: 4, padding: "4px 8px",
        }}>
          <Icon name="info" size={14} color={TK.mute} />
          Lihat detail penalaran
          <Icon name="chev" size={14} color={TK.mute} />
        </button>
      </Sheet>
    </>
  );
};

Object.assign(window, { VariantA, VariantB });

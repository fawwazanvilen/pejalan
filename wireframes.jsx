// Each WF is a phone-sized capture overlay wireframe.
// Top: camera viewfinder area with the just-captured photo + bounding box.
// Bottom: the result sheet (the main thing we're designing).

const PHONE_W = 360;
const PHONE_H = 760;

// ----- shared viewfinder top -----
const ViewfinderTop = ({ height = 320, bboxLabel = "motor parkir · 0.4m sisa lebar", category = "parkir" }) => (
  <div style={{ position: "relative", width: PHONE_W, height, background: "#2a2824" }}>
    {/* status bar */}
    <div style={{
      position: "absolute", top: 0, left: 0, right: 0, height: 24,
      display: "flex", justifyContent: "space-between", alignItems: "center", padding: "0 16px",
      fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: "#d9d5cc",
    }}>
      <span>09:42</span>
      <span>·  ·  ·</span>
    </div>
    {/* faux photo: dark hatching with vague sidewalk gradient */}
    <svg width={PHONE_W} height={height} style={{ position: "absolute", inset: 0 }}>
      <defs>
        <linearGradient id="sidewalk" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#3a3631" />
          <stop offset="60%" stopColor="#5b554c" />
          <stop offset="100%" stopColor="#7a7268" />
        </linearGradient>
        <pattern id="grit" patternUnits="userSpaceOnUse" width="6" height="6">
          <circle cx="1" cy="1" r="0.6" fill="#000" opacity="0.18" />
          <circle cx="4" cy="3" r="0.4" fill="#fff" opacity="0.06" />
        </pattern>
      </defs>
      <rect x="0" y="24" width={PHONE_W} height={height - 24} fill="url(#sidewalk)" />
      <rect x="0" y="24" width={PHONE_W} height={height - 24} fill="url(#grit)" />
      {/* perspective lines suggesting a sidewalk edge */}
      <path d={`M 40 ${height} L 150 ${height * 0.45} L 210 ${height * 0.45} L 320 ${height}`}
        fill="#3a3631" opacity="0.5" stroke="#1a1a1a" strokeWidth="0.6" />
      {/* faint guiding-block dashes for difabel variant */}
      {category === "difabel" && (
        <g opacity="0.55">
          {Array.from({ length: 6 }).map((_, i) => (
            <rect key={i} x={140 + i * 5} y={height - 90 - i * 22} width={50 - i * 6} height={4 + i} fill="#c9a04a" />
          ))}
        </g>
      )}
      {/* parked motor blob for parkir variant */}
      {category === "parkir" && (
        <g>
          <ellipse cx="190" cy={height - 70} rx="62" ry="14" fill="#000" opacity="0.4" />
          <rect x="148" y={height - 130} width="92" height="60" rx="6" fill="#2a2824" stroke="#0a0a0a" />
          <circle cx="165" cy={height - 78} r="14" fill="#1a1a1a" stroke="#0a0a0a" />
          <circle cx="225" cy={height - 78} r="14" fill="#1a1a1a" stroke="#0a0a0a" />
          <rect x="172" y={height - 140} width="44" height="14" fill="#3a3631" />
        </g>
      )}
      {/* bounding box */}
      <BBox
        x={category === "parkir" ? 130 : 132}
        y={category === "parkir" ? height - 158 : height - 132}
        w={category === "parkir" ? 130 : 96}
        h={category === "parkir" ? 110 : 72}
        label={bboxLabel}
        color="#f5d000"
      />
      {/* camera reticle hint */}
      <g opacity="0.5" stroke="#fff" strokeWidth="1" fill="none">
        <path d="M 12 36 L 12 24 L 24 24" />
        <path d={`M ${PHONE_W - 12} 36 L ${PHONE_W - 12} 24 L ${PHONE_W - 24} 24`} />
      </g>
    </svg>
    {/* tiny "menganalisis selesai" badge */}
    <div style={{
      position: "absolute", top: 36, left: 12,
      padding: "4px 8px", background: "rgba(20,20,20,0.7)", color: "#f5d000",
      fontFamily: "'JetBrains Mono', monospace", fontSize: 9, letterSpacing: 0.6,
      border: "1px solid #f5d000",
    }}>
      ● ANALISIS SELESAI · 0.8s
    </div>
  </div>
);

// ----- category icon + name row -----
const CategoryRow = ({ icon, name, size = "lg" }) => (
  <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
    <div style={{
      width: size === "lg" ? 44 : 32, height: size === "lg" ? 44 : 32,
      display: "flex", alignItems: "center", justifyContent: "center",
      position: "relative",
    }}>
      <div style={{ position: "absolute", inset: 0 }}>
        <WobbleBox w={size === "lg" ? 44 : 32} h={size === "lg" ? 44 : 32} sw={1.4} rx={size === "lg" ? 10 : 8} />
      </div>
      <div style={{ position: "relative" }}>
        <Icon name={icon} size={size === "lg" ? 24 : 18} />
      </div>
    </div>
    <div style={{ fontFamily: "'Kalam', cursive", fontWeight: 700, fontSize: size === "lg" ? 19 : 15, color: WF_INK, lineHeight: 1.1, letterSpacing: 0.3 }}>
      {name}
    </div>
  </div>
);

// Sheet header: little drag handle + label
const SheetHandle = () => (
  <div style={{ display: "flex", justifyContent: "center", paddingTop: 8, paddingBottom: 4 }}>
    <div style={{ width: 42, height: 4, borderRadius: 2, background: "#c8c2b6" }} />
  </div>
);

// ============================================================
//  WIREFRAME A — high-confidence happy path
// ============================================================
const WireframeA = () => {
  const top = 300;
  return (
    <PhoneFrame width={PHONE_W} height={PHONE_H}>
      <ViewfinderTop height={top} category="parkir" />
      {/* sheet */}
      <div style={{
        position: "absolute", left: 0, right: 0, top: top - 16, bottom: 0,
        background: WF_PAPER, borderTopLeftRadius: 22, borderTopRightRadius: 22,
        boxShadow: "0 -8px 0 rgba(0,0,0,0.04)",
        borderTop: `2px solid ${WF_INK}`,
        padding: "0 18px 20px",
      }}>
        <SheetHandle />

        {/* top row: category + confidence */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginTop: 6 }}>
          <CategoryRow icon="car" name={<>parkir liar</>} />
          <div style={{ textAlign: "right", fontFamily: "'Kalam', cursive" }}>
            <div style={{ fontSize: 11, color: WF_MUTED, letterSpacing: 0.4, textTransform: "uppercase" }}>yakin</div>
            <div style={{ fontSize: 28, fontWeight: 700, color: WF_INK, lineHeight: 1 }}>97%</div>
          </div>
        </div>

        {/* severity */}
        <div style={{ marginTop: 10 }}>
          <SeverityPill level="tinggi" />
        </div>

        {/* rationale */}
        <div style={{
          marginTop: 12, padding: "10px 12px", background: "#f1ede2",
          fontFamily: "'Kalam', cursive", fontSize: 14, lineHeight: 1.35, color: WF_INK,
          borderLeft: `3px solid ${WF_INK}`,
        }}>
          “trotoar tersumbat oleh parkir liar, pejalan kaki harus turun ke jalan.”
        </div>

        {/* chips — subtle in high-conf */}
        <div style={{ marginTop: 14 }}>
          <div style={{ fontFamily: "'Kalam', cursive", fontSize: 11, color: WF_MUTED, letterSpacing: 0.6, textTransform: "uppercase", marginBottom: 6 }}>
            bukan ini? ganti kategori:
          </div>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <Chip w={108}>halangan permanen</Chip>
            <Chip w={108}>trotoar rusak</Chip>
            <Chip w={70}>lainnya</Chip>
          </div>
        </div>

        {/* voice memo */}
        <div style={{ marginTop: 14, display: "flex", alignItems: "center", gap: 10 }}>
          <div style={{ position: "relative", width: 44, height: 44 }}>
            <WobbleBox w={44} h={44} sw={1.6} rx={22} />
            <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="mic" size={22} />
            </div>
          </div>
          <div style={{ fontFamily: "'Kalam', cursive", fontSize: 13, color: WF_INK }}>tambah catatan suara <span style={{ color: WF_MUTED }}>· max 30 dtk</span></div>
        </div>

        {/* primary action */}
        <div style={{ marginTop: 16, display: "flex", justifyContent: "center" }}>
          <PrimaryButton w={PHONE_W - 36}>lanjutkan ✓</PrimaryButton>
        </div>

        {/* tertiary */}
        <div style={{ marginTop: 10, display: "flex", justifyContent: "center", gap: 6, alignItems: "center",
          fontFamily: "'Kalam', cursive", fontSize: 13, color: WF_INK, textDecoration: "underline", textUnderlineOffset: 3 }}>
          lihat detail penalaran <Icon name="chev" size={14} />
        </div>
      </div>
    </PhoneFrame>
  );
};

// ============================================================
//  WIREFRAME B — medium-confidence, chips emphasized
// ============================================================
const WireframeB = () => {
  const top = 300;
  return (
    <PhoneFrame width={PHONE_W} height={PHONE_H}>
      <ViewfinderTop height={top} category="difabel" bboxLabel="ubin difabel · pola hilang" />
      <div style={{
        position: "absolute", left: 0, right: 0, top: top - 16, bottom: 0,
        background: WF_PAPER, borderTopLeftRadius: 22, borderTopRightRadius: 22,
        borderTop: `2px solid ${WF_INK}`,
        padding: "0 18px 20px",
      }}>
        <SheetHandle />

        {/* low-confidence banner */}
        <div style={{
          marginTop: 4, padding: "6px 10px",
          background: "#fff2c8", border: `1.4px dashed ${WF_INK}`,
          fontFamily: "'Kalam', cursive", fontSize: 12, color: WF_INK,
          display: "flex", alignItems: "center", gap: 6,
        }}>
          <Icon name="info" size={14} /> agak ragu — tolong cek & pilih yang benar
        </div>

        {/* top row: category + confidence */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginTop: 10 }}>
          <CategoryRow icon="tiles" name={<>ubin difabel<br />bermasalah</>} />
          <div style={{ textAlign: "right", fontFamily: "'Kalam', cursive" }}>
            <div style={{ fontSize: 11, color: WF_MUTED, letterSpacing: 0.4, textTransform: "uppercase" }}>yakin</div>
            <div style={{ fontSize: 28, fontWeight: 700, color: "#c08a3e", lineHeight: 1 }}>64%</div>
            {/* meter */}
            <svg width="74" height="6" style={{ marginTop: 4 }}>
              <rect x="0" y="0" width="74" height="6" fill="#eee5cc" />
              <rect x="0" y="0" width={74 * 0.64} height="6" fill="#c08a3e" />
              <line x1="0" y1="3" x2="74" y2="3" stroke={WF_INK} strokeWidth="0.5" opacity="0.2" />
            </svg>
          </div>
        </div>

        <div style={{ marginTop: 10 }}>
          <SeverityPill level="sedang" />
        </div>

        <div style={{
          marginTop: 10, padding: "8px 10px", background: "#f1ede2",
          fontFamily: "'Kalam', cursive", fontSize: 13, lineHeight: 1.3, color: WF_INK,
          borderLeft: `3px solid ${WF_INK}`,
        }}>
          “pola ubin pemandu tidak konsisten, penyandang disabilitas netra bisa tersesat.”
        </div>

        {/* CHIPS — emphasized */}
        <div style={{
          marginTop: 14, padding: "10px 10px 12px", position: "relative",
        }}>
          <div style={{ position: "absolute", inset: 0 }}>
            <WobbleBox w={PHONE_W - 36} h={134} sw={1.6} rx={10} fill="#fffaf0" />
          </div>
          <div style={{ position: "relative" }}>
            <div style={{ fontFamily: "'Kalam', cursive", fontSize: 12, fontWeight: 700, color: WF_INK, marginBottom: 8, display: "flex", alignItems: "center", gap: 6 }}>
              <Icon name="edit" size={14} /> apakah sebenarnya ini?
            </div>
            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              <div style={{ position: "relative", height: 36, display: "flex", alignItems: "center", paddingLeft: 12, gap: 8 }}>
                <div style={{ position: "absolute", inset: 0 }}><WobbleBox w={PHONE_W - 56} h={36} sw={2.2} rx={8} fill="#fff7d6" /></div>
                <div style={{ position: "relative", display: "flex", alignItems: "center", gap: 8 }}>
                  <Icon name="tiles" size={16} />
                  <span style={{ fontFamily: "'Kalam', cursive", fontSize: 14, fontWeight: 700 }}>ubin difabel bermasalah</span>
                  <span style={{ marginLeft: 6, fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: WF_MUTED }}>64%</span>
                </div>
              </div>
              <div style={{ position: "relative", height: 32, display: "flex", alignItems: "center", paddingLeft: 12, gap: 8 }}>
                <div style={{ position: "absolute", inset: 0 }}><WobbleBox w={PHONE_W - 56} h={32} sw={1.3} rx={8} /></div>
                <div style={{ position: "relative", display: "flex", alignItems: "center", gap: 8 }}>
                  <Icon name="crack" size={16} />
                  <span style={{ fontFamily: "'Kalam', cursive", fontSize: 13 }}>trotoar rusak</span>
                  <span style={{ marginLeft: 6, fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: WF_MUTED }}>21%</span>
                </div>
              </div>
              <div style={{ position: "relative", height: 32, display: "flex", alignItems: "center", paddingLeft: 12, gap: 8 }}>
                <div style={{ position: "absolute", inset: 0 }}><WobbleBox w={PHONE_W - 56} h={32} sw={1.3} rx={8} /></div>
                <div style={{ position: "relative", display: "flex", alignItems: "center", gap: 8 }}>
                  <Icon name="drain" size={16} />
                  <span style={{ fontFamily: "'Kalam', cursive", fontSize: 13 }}>drainase</span>
                  <span style={{ marginLeft: 6, fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: WF_MUTED }}>9%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* footer row: mic + primary side by side */}
        <div style={{ marginTop: 14, display: "flex", gap: 10, alignItems: "center" }}>
          <div style={{ position: "relative", width: 52, height: 52 }}>
            <WobbleBox w={52} h={52} sw={1.6} rx={26} />
            <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="mic" size={22} />
            </div>
          </div>
          <PrimaryButton w={PHONE_W - 36 - 52 - 10} h={52}>lanjutkan</PrimaryButton>
        </div>

        <div style={{ marginTop: 8, display: "flex", justifyContent: "center", gap: 6, alignItems: "center",
          fontFamily: "'Kalam', cursive", fontSize: 13, color: WF_INK, textDecoration: "underline", textUnderlineOffset: 3 }}>
          lihat detail penalaran <Icon name="chev" size={14} />
        </div>
      </div>
    </PhoneFrame>
  );
};

// ============================================================
//  ALT 1 — Confidence as a meter (no percentage), category-first
// ============================================================
const WireframeAlt1 = () => {
  const top = 280;
  return (
    <PhoneFrame width={PHONE_W} height={PHONE_H}>
      <ViewfinderTop height={top} category="parkir" />
      <div style={{
        position: "absolute", left: 0, right: 0, top: top - 16, bottom: 0,
        background: WF_PAPER, borderTopLeftRadius: 22, borderTopRightRadius: 22,
        borderTop: `2px solid ${WF_INK}`,
        padding: "0 18px 20px",
      }}>
        <SheetHandle />

        {/* big category header, no percentage */}
        <div style={{ marginTop: 6 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
            <div style={{ position: "relative", width: 56, height: 56 }}>
              <WobbleBox w={56} h={56} sw={1.8} rx={12} fill="#f1ede2" />
              <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center" }}>
                <Icon name="car" size={30} />
              </div>
            </div>
            <div>
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: WF_MUTED, letterSpacing: 0.6 }}>KATEGORI</div>
              <div style={{ fontFamily: "'Kalam', cursive", fontWeight: 700, fontSize: 22, color: WF_INK, lineHeight: 1 }}>parkir liar</div>
            </div>
          </div>

          {/* confidence dots — 5-step meter */}
          <div style={{ marginTop: 14, display: "flex", alignItems: "center", gap: 8 }}>
            <div style={{ fontFamily: "'Kalam', cursive", fontSize: 12, color: WF_MUTED, width: 64 }}>tingkat<br/>keyakinan</div>
            <div style={{ display: "flex", gap: 4, flex: 1 }}>
              {[1, 2, 3, 4, 5].map((i) => (
                <div key={i} style={{
                  flex: 1, height: 12,
                  background: i <= 5 ? "#0d5b5b" : "transparent",
                  border: `1.4px solid ${WF_INK}`,
                  opacity: i <= 5 ? 1 : 0.4,
                }} />
              ))}
            </div>
            <div style={{ fontFamily: "'Kalam', cursive", fontSize: 13, fontWeight: 700 }}>sangat yakin</div>
          </div>
        </div>

        <div style={{ marginTop: 14, display: "flex", alignItems: "center", gap: 10 }}>
          <SeverityPill level="tinggi" />
          <span style={{ fontFamily: "'Kalam', cursive", fontSize: 12, color: WF_MUTED }}>· trotoar terblokir total</span>
        </div>

        <div style={{
          marginTop: 12, padding: "10px 12px", background: "#f1ede2",
          fontFamily: "'Kalam', cursive", fontSize: 13, lineHeight: 1.35, color: WF_INK,
          borderLeft: `3px solid ${WF_INK}`,
        }}>
          “trotoar tersumbat oleh parkir liar, pejalan kaki harus turun ke jalan.”
        </div>

        <div style={{ marginTop: 12, fontFamily: "'Kalam', cursive", fontSize: 11, color: WF_MUTED, letterSpacing: 0.6, textTransform: "uppercase" }}>
          bukan ini?
        </div>
        <div style={{ marginTop: 6, display: "flex", gap: 8, flexWrap: "wrap" }}>
          <Chip w={108}>halangan permanen</Chip>
          <Chip w={108}>trotoar rusak</Chip>
          <Chip w={70}>lainnya</Chip>
        </div>

        <div style={{ marginTop: 16, display: "flex", gap: 10, alignItems: "center" }}>
          <div style={{ position: "relative", width: 52, height: 52 }}>
            <WobbleBox w={52} h={52} sw={1.6} rx={26} />
            <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="mic" size={22} />
            </div>
          </div>
          <PrimaryButton w={PHONE_W - 36 - 52 - 10} h={52} tone="teal">lanjutkan</PrimaryButton>
        </div>
      </div>
    </PhoneFrame>
  );
};

// ============================================================
//  ALT 2 — Rationale FIRST (AI's reasoning leads), category demoted
// ============================================================
const WireframeAlt2 = () => {
  const top = 290;
  return (
    <PhoneFrame width={PHONE_W} height={PHONE_H}>
      <ViewfinderTop height={top} category="parkir" />
      <div style={{
        position: "absolute", left: 0, right: 0, top: top - 16, bottom: 0,
        background: WF_PAPER, borderTopLeftRadius: 22, borderTopRightRadius: 22,
        borderTop: `2px solid ${WF_INK}`,
        padding: "0 18px 20px",
      }}>
        <SheetHandle />

        {/* big rationale takes the stage */}
        <div style={{ marginTop: 6, fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: WF_MUTED, letterSpacing: 0.6 }}>
          AI MELIHAT:
        </div>
        <div style={{
          marginTop: 6,
          fontFamily: "'Kalam', cursive", fontSize: 20, fontWeight: 700, lineHeight: 1.25, color: WF_INK,
        }}>
          “trotoar tersumbat oleh parkir liar — pejalan kaki harus turun ke jalan.”
        </div>

        {/* tag row */}
        <div style={{ marginTop: 14, display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
          <div style={{ display: "inline-flex", alignItems: "center", gap: 6, padding: "5px 10px", border: `1.6px solid ${WF_INK}`, borderRadius: 16, fontFamily: "'Kalam', cursive", fontSize: 13, fontWeight: 700 }}>
            <Icon name="car" size={16} /> parkir liar
          </div>
          <SeverityPill level="tinggi" />
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: WF_MUTED }}>· 97% yakin</span>
        </div>

        {/* divider */}
        <div style={{ marginTop: 16, borderTop: `1.4px dashed ${WF_INK}`, opacity: 0.3 }} />

        <div style={{ marginTop: 12, fontFamily: "'Kalam', cursive", fontSize: 11, color: WF_MUTED, letterSpacing: 0.6, textTransform: "uppercase" }}>
          jika salah, koreksi:
        </div>
        <div style={{ marginTop: 6, display: "flex", gap: 8, flexWrap: "wrap" }}>
          <Chip w={108}>halangan permanen</Chip>
          <Chip w={108}>trotoar rusak</Chip>
          <Chip w={70}>lainnya</Chip>
        </div>

        <div style={{ marginTop: 14, display: "flex", alignItems: "center", gap: 10 }}>
          <div style={{ position: "relative", width: 40, height: 40 }}>
            <WobbleBox w={40} h={40} sw={1.4} rx={20} />
            <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="mic" size={20} />
            </div>
          </div>
          <div style={{ fontFamily: "'Kalam', cursive", fontSize: 13, color: WF_INK }}>+ catatan suara</div>
          <div style={{ marginLeft: "auto", display: "flex", gap: 6, alignItems: "center",
            fontFamily: "'Kalam', cursive", fontSize: 12, color: WF_INK, textDecoration: "underline" }}>
            detail <Icon name="chev" size={12} />
          </div>
        </div>

        <div style={{ marginTop: 14 }}>
          <PrimaryButton w={PHONE_W - 36}>lanjutkan ✓</PrimaryButton>
        </div>
      </div>
    </PhoneFrame>
  );
};

// ============================================================
//  ALT 3 — Minimal sheet: ONE tap to confirm, secondary "ubah"
// ============================================================
const WireframeAlt3 = () => {
  const top = 380;
  return (
    <PhoneFrame width={PHONE_W} height={PHONE_H}>
      <ViewfinderTop height={top} category="parkir" />
      <div style={{
        position: "absolute", left: 0, right: 0, top: top - 16, bottom: 0,
        background: WF_PAPER, borderTopLeftRadius: 22, borderTopRightRadius: 22,
        borderTop: `2px solid ${WF_INK}`,
        padding: "0 18px 20px",
      }}>
        <SheetHandle />

        {/* tight summary card */}
        <div style={{
          marginTop: 8, padding: "12px 14px", position: "relative",
        }}>
          <div style={{ position: "absolute", inset: 0 }}>
            <WobbleBox w={PHONE_W - 36} h={92} sw={1.8} rx={10} fill="#f1ede2" />
          </div>
          <div style={{ position: "relative", display: "flex", gap: 12, alignItems: "center" }}>
            <div style={{ position: "relative", width: 48, height: 48 }}>
              <Icon name="car" size={36} />
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontFamily: "'Kalam', cursive", fontWeight: 700, fontSize: 18, color: WF_INK, lineHeight: 1 }}>parkir liar</div>
              <div style={{ marginTop: 4, display: "flex", alignItems: "center", gap: 8 }}>
                <SeverityPill level="tinggi" />
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: WF_MUTED }}>97%</span>
              </div>
            </div>
          </div>
          <div style={{ position: "relative", marginTop: 8, fontFamily: "'Kalam', cursive", fontSize: 12, color: "#3a3631", lineHeight: 1.3 }}>
            trotoar tersumbat — pejalan harus turun ke jalan
          </div>
        </div>

        <div style={{ marginTop: 14, display: "flex", gap: 10 }}>
          <div style={{ flex: 1, position: "relative", height: 48, display: "flex", alignItems: "center", justifyContent: "center", gap: 6 }}>
            <WobbleBox w={(PHONE_W - 36 - 10) / 2} h={48} sw={1.6} rx={8} />
            <span style={{ position: "relative", fontFamily: "'Kalam', cursive", fontSize: 14 }}>ubah kategori</span>
          </div>
          <div style={{ flex: 1, position: "relative", height: 48, display: "flex", alignItems: "center", justifyContent: "center", gap: 6 }}>
            <WobbleBox w={(PHONE_W - 36 - 10) / 2} h={48} sw={1.6} rx={8} />
            <Icon name="mic" size={18} />
            <span style={{ position: "relative", fontFamily: "'Kalam', cursive", fontSize: 14 }}>catatan</span>
          </div>
        </div>

        <div style={{ marginTop: 12 }}>
          <PrimaryButton w={PHONE_W - 36} h={56}>simpan & lanjut ✓</PrimaryButton>
        </div>

        <div style={{ marginTop: 10, display: "flex", justifyContent: "space-between",
          fontFamily: "'Kalam', cursive", fontSize: 12, color: WF_MUTED }}>
          <span>↶ ambil ulang</span>
          <span style={{ textDecoration: "underline", color: WF_INK }}>lihat detail →</span>
        </div>
      </div>
    </PhoneFrame>
  );
};

Object.assign(window, { WireframeA, WireframeB, WireframeAlt1, WireframeAlt2, WireframeAlt3 });

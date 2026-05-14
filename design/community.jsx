// Community / map / profile / recognition screens.
// Civic, non-extractive gamification: recognition over competition, collective over individual.

const PHOTO__ = "assets/mobil-trotoar.png";

// ============================================================
// 09 · LINIMASA — community feed of recent laporan
// ============================================================
const FeedItem = ({ initials, name, when, area, cat, sev, blurb, accent, hue }) => (
  <div style={{ background: M.paperHi, border: `1.4px solid ${M.ink}`, padding: "12px 12px 14px", display: "flex", flexDirection: "column", gap: 8 }}>
    {/* header */}
    <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
      <div style={{ width: 32, height: 32, background: hue, color: M.paperHi, fontFamily: M.fDisplay, fontWeight: 800, fontSize: 13, display: "flex", alignItems: "center", justifyContent: "center", border: `1.4px solid ${M.ink}` }}>
        {initials}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 13, color: M.ink, lineHeight: 1.1 }}>{name}</div>
        <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.2, color: M.mute, textTransform: "uppercase" }}>{area} · {when}</div>
      </div>
      <SevStamp level={sev} rot={-2}/>
    </div>
    {/* mini photo placeholder w/ category strip */}
    <div style={{ position: "relative", height: 110, background: M.paperLo, border: `1.2px solid ${M.muteLo}`, overflow: "hidden", display: "flex", alignItems: "center", justifyContent: "center" }}>
      <svg width="100%" height="100%" viewBox="0 0 200 110" preserveAspectRatio="none">
        <defs>
          <pattern id={`pf-${initials}`} width="14" height="14" patternUnits="userSpaceOnUse" patternTransform="rotate(45)">
            <line x1="0" y1="0" x2="0" y2="14" stroke={M.muteLo} strokeWidth="1"/>
          </pattern>
        </defs>
        <rect width="200" height="110" fill={`url(#pf-${initials})`}/>
      </svg>
      <div style={{ position: "absolute", top: 6, left: 6, padding: "2px 6px", background: accent, color: M.paperHi, fontFamily: M.fMono, fontSize: 9.5, fontWeight: 700, letterSpacing: 1.2, textTransform: "uppercase" }}>{cat}</div>
      <div style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center", fontFamily: M.fMono, fontSize: 10, color: M.mute, letterSpacing: 1.2 }}>FOTO LAPORAN</div>
    </div>
    <div style={{ fontFamily: M.fDisplay, fontSize: 12.5, lineHeight: 1.4, color: M.inkSoft }}>{blurb}</div>
    {/* reactions */}
    <div style={{ display: "flex", gap: 6, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.2, color: M.mute, textTransform: "uppercase" }}>
      <span style={{ padding: "3px 7px", border: `1.2px solid ${M.muteLo}`, color: M.ink }}>✓ akurat · 12</span>
      <span style={{ padding: "3px 7px", border: `1.2px solid ${M.muteLo}` }}>jl. yang sama · 3</span>
    </div>
  </div>
);

const ScreenFeed = () => (
  <Phone label="09" sub="Linimasa komunitas">
    <div style={{ position: "absolute", inset: 0, background: M.paper, color: M.ink, padding: "40px 16px 80px", overflowY: "auto" }}>
      {/* header */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
        <div>
          <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.6, color: M.mute, textTransform: "uppercase" }}>Linimasa · jakarta</div>
          <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 28, lineHeight: 1, letterSpacing: -0.6, textTransform: "lowercase", marginTop: 2 }}>hari ini di trotoar.</div>
        </div>
        <span style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.teal }}>247 LAPORAN</span>
      </div>

      {/* filter chips */}
      <div style={{ display: "flex", gap: 6, marginTop: 12, overflowX: "auto", paddingBottom: 4 }}>
        {[
          { l: "semua", sel: true },
          { l: "menteng" }, { l: "tanah abang" }, { l: "kebon sirih" }, { l: "kuningan" },
        ].map((c, i) => (
          <span key={i} style={{ flexShrink: 0, padding: "5px 10px", background: c.sel ? M.ink : "transparent", color: c.sel ? M.paperHi : M.ink, border: `1.4px solid ${M.ink}`, fontFamily: M.fMono, fontSize: 11, fontWeight: 600, letterSpacing: 0.8, textTransform: "uppercase" }}>{c.l}</span>
        ))}
      </div>

      {/* feed */}
      <div style={{ display: "flex", flexDirection: "column", gap: 10, marginTop: 14 }}>
        <FeedItem initials="AR" name="Ayu Ratnasari" when="14 menit lalu" area="JL. SABANG" cat="parkir liar" sev="tinggi" accent={M.ox} hue={M.teal}
          blurb="Tiga motor parkir penuh di trotoar depan warteg, pejalan turun ke jalan saat jam makan siang."/>
        <FeedItem initials="DM" name="Dimas M." when="32 menit lalu" area="JL. WAHID HASYIM" cat="trotoar rusak" sev="sedang" accent={M.amber} hue={M.amber}
          blurb="Ubin paving lepas sekitar 2 meter, dudukan halte mulai retak."/>
        <FeedItem initials="KP" name="KPK · relawan #14" when="1 jam lalu" area="JL. THAMRIN" cat="ubin difabel" sev="sedang" accent={M.amber} hue={M.ox}
          blurb="Guiding block hilang di depan stasiun, jalur netra terputus 6 meter."/>
        <FeedItem initials="SH" name="Sari Handayani" when="2 jam lalu" area="JL. KEBON KACANG" cat="drainase" sev="rendah" accent={M.olive} hue={M.olive}
          blurb="Tutup got hilang, sudah ditandai dengan ranting. Belum ada pengganti."/>
      </div>

      <div style={{ marginTop: 14, padding: 10, textAlign: "center", border: `1.4px dashed ${M.muteLo}`, fontFamily: M.fMono, fontSize: 11, letterSpacing: 1.2, color: M.mute, textTransform: "uppercase" }}>
        muat lebih banyak →
      </div>
    </div>

    {/* tab bar */}
    <BottomTabs active="linimasa"/>
  </Phone>
);

// ============================================================
// 10 · PETA — map view, density of audits
// ============================================================
const MapMarker = ({ x, y, sev, sz = 8 }) => {
  const c = sev === "tinggi" ? M.ox : sev === "sedang" ? M.amber : M.olive;
  return (
    <g transform={`translate(${x},${y})`}>
      <circle r={sz + 4} fill={c} opacity="0.18"/>
      <circle r={sz} fill={c} stroke={M.paper} strokeWidth="1.4"/>
    </g>
  );
};

const ScreenMap = () => {
  // pseudo-random pins for Jakarta-ish layout
  const pins = [
    [120, 180, "tinggi"], [160, 210, "sedang"], [210, 180, "tinggi"], [195, 240, "rendah"],
    [240, 290, "sedang"], [180, 320, "tinggi"], [150, 360, "sedang"], [220, 380, "rendah"],
    [110, 270, "sedang"], [90, 220, "rendah"], [260, 230, "tinggi"], [280, 320, "sedang"],
    [170, 410, "rendah"], [230, 430, "sedang"], [310, 260, "rendah"], [70, 320, "tinggi"],
    [340, 200, "sedang"], [200, 460, "rendah"], [130, 460, "sedang"], [290, 410, "tinggi"],
  ];
  return (
    <Phone label="10" sub="Peta audit · jakarta pusat">
      <div style={{ position: "absolute", inset: 0, background: M.paper, color: M.ink, paddingTop: 40, display: "flex", flexDirection: "column" }}>
        {/* header */}
        <div style={{ padding: "0 16px 10px" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
            <div>
              <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.6, color: M.mute, textTransform: "uppercase" }}>peta · 7 hari terakhir</div>
              <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 26, lineHeight: 1, letterSpacing: -0.5, textTransform: "lowercase" }}>jakarta pusat.</div>
            </div>
            <div style={{ textAlign: "right" }}>
              <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 22, lineHeight: 1 }}>1,284</div>
              <div style={{ fontFamily: M.fMono, fontSize: 9.5, letterSpacing: 1.2, color: M.mute }}>LAPORAN</div>
            </div>
          </div>
        </div>

        {/* map */}
        <div style={{ flex: 1, position: "relative", margin: "0 16px", border: `1.5px solid ${M.ink}`, background: M.paperHi, overflow: "hidden" }}>
          {/* schematic streets */}
          <svg width="100%" height="100%" viewBox="0 0 390 540" preserveAspectRatio="xMidYMid slice" style={{ position: "absolute", inset: 0 }}>
            {/* grid */}
            <defs>
              <pattern id="mapgrid" width="40" height="40" patternUnits="userSpaceOnUse">
                <path d="M 40 0 L 0 0 0 40" stroke={M.muteLo} strokeWidth="0.5" fill="none" opacity="0.45"/>
              </pattern>
            </defs>
            <rect width="100%" height="100%" fill="url(#mapgrid)"/>
            {/* main roads */}
            <path d="M0 220 Q 180 250 390 240" stroke={M.muteLo} strokeWidth="3.5" fill="none"/>
            <path d="M0 340 Q 200 360 390 350" stroke={M.muteLo} strokeWidth="3.5" fill="none"/>
            <path d="M180 0 Q 200 270 220 540" stroke={M.muteLo} strokeWidth="3.5" fill="none"/>
            <path d="M70 0 Q 100 270 130 540" stroke={M.muteLo} strokeWidth="2" fill="none"/>
            <path d="M290 0 Q 310 270 330 540" stroke={M.muteLo} strokeWidth="2" fill="none"/>
            {/* labels */}
            <text x="40" y="218" fontFamily="'Barlow Semi Condensed'" fontSize="11" fontWeight="600" letterSpacing="1.4" fill={M.mute} textTransform="uppercase">JL. THAMRIN</text>
            <text x="40" y="338" fontFamily="'Barlow Semi Condensed'" fontSize="11" fontWeight="600" letterSpacing="1.4" fill={M.mute}>JL. SUDIRMAN</text>
            <text x="195" y="20" fontFamily="'Barlow Semi Condensed'" fontSize="10" fontWeight="600" letterSpacing="1.4" fill={M.mute}>JL. SABANG</text>
            {/* heatmap zones */}
            <ellipse cx="180" cy="240" rx="80" ry="55" fill={M.ox} opacity="0.1"/>
            <ellipse cx="220" cy="370" rx="70" ry="50" fill={M.amber} opacity="0.12"/>
            {/* pins */}
            {pins.map(([x, y, s], i) => <MapMarker key={i} x={x} y={y} sev={s} sz={s === "tinggi" ? 9 : 7}/>)}
            {/* your location pulse */}
            <g transform="translate(195, 285)">
              <circle r="18" fill={M.teal} opacity="0.18"/>
              <circle r="10" fill={M.teal} opacity="0.3"/>
              <circle r="5" fill={M.teal} stroke={M.paper} strokeWidth="1.5"/>
            </g>
          </svg>

          {/* legend */}
          <div style={{ position: "absolute", top: 10, right: 10, background: M.paper, border: `1.4px solid ${M.ink}`, padding: "6px 8px", display: "flex", flexDirection: "column", gap: 4, fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, textTransform: "uppercase" }}>
            <div style={{ display: "flex", alignItems: "center", gap: 6 }}><span style={{ width: 9, height: 9, background: M.ox, borderRadius: "50%" }}/>tinggi · 312</div>
            <div style={{ display: "flex", alignItems: "center", gap: 6 }}><span style={{ width: 9, height: 9, background: M.amber, borderRadius: "50%" }}/>sedang · 581</div>
            <div style={{ display: "flex", alignItems: "center", gap: 6 }}><span style={{ width: 9, height: 9, background: M.olive, borderRadius: "50%" }}/>rendah · 391</div>
          </div>

          {/* "you are here" callout */}
          <div style={{ position: "absolute", bottom: 70, left: 22, padding: "5px 8px", background: M.teal, color: M.paper, fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, textTransform: "uppercase", fontWeight: 700 }}>
            ANDA · KEBON SIRIH
          </div>
        </div>

        {/* bottom info card */}
        <div style={{ margin: "12px 16px 80px", padding: "10px 12px", background: M.paperHi, border: `1.5px solid ${M.ink}` }}>
          <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>Titik panas minggu ini</div>
          <div style={{ marginTop: 4, display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 16 }}>Jl. Sabang, depan warteg</div>
            <span style={{ fontFamily: M.fMono, fontSize: 11, fontWeight: 700, color: M.ox, letterSpacing: 1 }}>47 LAPORAN</span>
          </div>
          <div style={{ fontFamily: M.fDisplay, fontSize: 12, color: M.mute, marginTop: 2 }}>Disorot dalam laporan KPK Q3 — sedang ditindaklanjuti.</div>
        </div>
      </div>
      <BottomTabs active="peta"/>
    </Phone>
  );
};

// ============================================================
// 11 · PROFIL — personal stats, civic identity
// ============================================================
const ScreenProfile = () => (
  <Phone label="11" sub="Profil & kontribusi">
    <div style={{ position: "absolute", inset: 0, background: M.paper, color: M.ink, padding: "40px 18px 80px", overflowY: "auto" }}>
      {/* avatar + name */}
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <div style={{ width: 64, height: 64, background: M.teal, color: M.paper, fontFamily: M.fDisplay, fontWeight: 800, fontSize: 24, display: "flex", alignItems: "center", justifyContent: "center", border: `1.6px solid ${M.ink}` }}>BS</div>
        <div style={{ flex: 1 }}>
          <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>Pejalan · sejak Mar 2025</div>
          <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 22, lineHeight: 1, letterSpacing: -0.5 }}>Budi Santoso</div>
          <div style={{ fontFamily: M.fDisplay, fontSize: 12, color: M.mute, marginTop: 2 }}>Menteng · relawan KPK</div>
        </div>
        <button style={{ width: 36, height: 36, background: "transparent", border: `1.4px solid ${M.ink}`, color: M.ink, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }}>
          <Ic n="edit" s={18}/>
        </button>
      </div>

      {/* stats grid */}
      <div style={{ marginTop: 18, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
        {[
          { n: "342", l: "AUDIT" },
          { n: "58.4", l: "KM BERJALAN" },
          { n: "12", l: "HARI BERTURUT" },
          { n: "8", l: "JALAN DIPERBAIKI" },
        ].map((s, i) => (
          <div key={i} style={{ padding: "10px 12px", background: M.paperHi, border: `1.4px solid ${M.ink}` }}>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 28, lineHeight: 0.95, letterSpacing: -0.6, color: M.ink }}>{s.n}</div>
            <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.4, color: M.mute, marginTop: 3 }}>{s.l}</div>
          </div>
        ))}
      </div>

      {/* civic impact callout — the meaningful "score" */}
      <div style={{ marginTop: 14, padding: "12px 14px", background: M.teal, color: M.paper, position: "relative" }}>
        <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.8, color: M.tealTint, textTransform: "uppercase" }}>Dampak</div>
        <div style={{ marginTop: 4, fontFamily: M.fDisplay, fontSize: 16, lineHeight: 1.3, fontWeight: 500 }}>
          Laporan Anda turut dimasukkan ke dalam <b>3 dokumen advokasi</b> KPK & ITDP tahun ini.
        </div>
        <div style={{ marginTop: 8, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.2, color: M.tealTint, textTransform: "uppercase" }}>Lihat dokumen →</div>
      </div>

      {/* recent badges — recognition, not competition */}
      <div style={{ marginTop: 14 }}>
        <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>Lencana terbaru</div>
        <div style={{ marginTop: 8, display: "flex", gap: 8 }}>
          {[
            { ic: "loc", l: "Pemeta\nMenteng", c: M.teal },
            { ic: "tiles", l: "Sahabat\nDifabel", c: M.amber },
            { ic: "check", l: "12 hari\nberturut", c: M.olive },
          ].map((b, i) => (
            <div key={i} style={{ flex: 1, padding: "10px 8px", background: M.paperHi, border: `1.4px solid ${b.c}`, display: "flex", flexDirection: "column", alignItems: "center", gap: 6 }}>
              <div style={{ width: 32, height: 32, background: b.c, color: M.paperHi, display: "flex", alignItems: "center", justifyContent: "center", borderRadius: "50%" }}>
                <Ic n={b.ic} s={18} w={2.2}/>
              </div>
              <div style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 11, lineHeight: 1.15, textAlign: "center", whiteSpace: "pre-line" }}>{b.l}</div>
            </div>
          ))}
        </div>
      </div>

      {/* contributions per category */}
      <div style={{ marginTop: 14 }}>
        <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>Kontribusi per kategori</div>
        <div style={{ marginTop: 8, display: "flex", flexDirection: "column", gap: 4 }}>
          {[
            { l: "parkir liar", n: 142, c: M.ox },
            { l: "trotoar rusak", n: 87, c: M.amber },
            { l: "ubin difabel", n: 56, c: M.amber },
            { l: "halangan permanen", n: 31, c: M.olive },
            { l: "drainase", n: 26, c: M.olive },
          ].map((r, i) => (
            <div key={i} style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <div style={{ flex: 1, fontFamily: M.fDisplay, fontSize: 12, fontWeight: 500 }}>{r.l}</div>
              <div style={{ width: 100, height: 8, background: M.paperHi, border: `1px solid ${M.muteLo}`, position: "relative" }}>
                <div style={{ width: `${Math.min(100, (r.n / 142) * 100)}%`, height: "100%", background: r.c }}/>
              </div>
              <div style={{ fontFamily: M.fMono, fontSize: 11, fontWeight: 700, width: 32, textAlign: "right" }}>{r.n}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
    <BottomTabs active="profil"/>
  </Phone>
);

// ============================================================
// 12 · LENCANA / RECOGNITION — gamification, civic-respectful
// ============================================================
const ScreenBadges = () => (
  <Phone label="12" sub="Lencana & pengakuan komunitas">
    <div style={{ position: "absolute", inset: 0, background: M.paper, color: M.ink, padding: "40px 18px 80px", overflowY: "auto" }}>
      <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.6, color: M.mute, textTransform: "uppercase" }}>Lencana</div>
      <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 26, lineHeight: 1, letterSpacing: -0.5, textTransform: "lowercase", marginTop: 2 }}>
        terima kasih atas<br/>kerja sipilmu.
      </div>

      {/* THIS week's collective callout — community first */}
      <div style={{ marginTop: 14, padding: "12px 14px", background: M.paperHi, border: `1.5px solid ${M.ink}`, position: "relative" }}>
        <div style={{ display: "inline-block", padding: "3px 8px", background: M.teal, color: M.paper, fontFamily: M.fMono, fontSize: 10, fontWeight: 700, letterSpacing: 1.6, textTransform: "uppercase", transform: "rotate(-1.5deg)" }}>Minggu ini · bersama</div>
        <div style={{ marginTop: 8, display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
          <div>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 32, lineHeight: 0.95, letterSpacing: -0.7 }}>1,847</div>
            <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, color: M.mute }}>AUDIT KOMUNITAS</div>
          </div>
          <div>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 32, lineHeight: 0.95, letterSpacing: -0.7 }}>312</div>
            <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, color: M.mute }}>PEJALAN AKTIF</div>
          </div>
          <div>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 32, lineHeight: 0.95, letterSpacing: -0.7, color: M.teal }}>4</div>
            <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, color: M.mute }}>JALAN DIPERBAIKI</div>
          </div>
        </div>
        <div style={{ marginTop: 8, fontFamily: M.fDisplay, fontSize: 12.5, lineHeight: 1.4, color: M.inkSoft }}>
          Audit Jl. Sabang yang Anda buat 11 hari lalu termasuk dalam laporan KPK ke Pemprov DKI.
        </div>
      </div>

      {/* All badges grid */}
      <div style={{ marginTop: 16, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>Lencana yang sudah didapat</div>
      <div style={{ marginTop: 8, display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 6 }}>
        {[
          { ic: "loc",   l: "Pemeta Menteng",   d: "Audit 5+ jalan", c: M.teal, got: true },
          { ic: "tiles", l: "Sahabat Difabel",  d: "30+ laporan ubin", c: M.amber, got: true },
          { ic: "check", l: "12 hari berturut", d: "Streak terjaga", c: M.olive, got: true },
          { ic: "crack", l: "Mata Tajam",       d: "Akurasi 95%+",   c: M.teal, got: true },
          { ic: "car",   l: "Penjaga Trotoar",  d: "100+ parkir liar", c: M.ox, got: true },
          { ic: "drain", l: "Anti-Banjir",      d: "20+ drainase",   c: M.muteLo, got: false },
        ].map((b, i) => (
          <div key={i} style={{ padding: "10px 8px", background: b.got ? M.paperHi : "transparent", border: `1.4px solid ${b.got ? b.c : M.muteLo}`, display: "flex", flexDirection: "column", alignItems: "center", gap: 6, opacity: b.got ? 1 : 0.5 }}>
            <div style={{ width: 32, height: 32, background: b.got ? b.c : M.muteLo, color: M.paperHi, display: "flex", alignItems: "center", justifyContent: "center", borderRadius: "50%" }}>
              <Ic n={b.ic} s={18} w={2.2}/>
            </div>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 11, lineHeight: 1.15, textAlign: "center" }}>{b.l}</div>
            <div style={{ fontFamily: M.fMono, fontSize: 9, letterSpacing: 1, color: M.mute, textTransform: "uppercase", textAlign: "center", lineHeight: 1.3 }}>{b.d}</div>
          </div>
        ))}
      </div>

      {/* Community recognition — NOT a leaderboard */}
      <div style={{ marginTop: 16, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>Sorotan komunitas minggu ini</div>
      <div style={{ marginTop: 8, display: "flex", flexDirection: "column", gap: 6 }}>
        {[
          { i: "AR", n: "Ayu Ratnasari",  why: "Memetakan seluruh Sabang",     hue: M.teal },
          { i: "KP", n: "KPK · relawan",  why: "Audit difabel paling lengkap", hue: M.ox },
          { i: "SH", n: "Sari Handayani", why: "Konsistensi 30 hari",          hue: M.amber },
        ].map((r, i) => (
          <div key={i} style={{ display: "flex", alignItems: "center", gap: 10, padding: "8px 10px", background: M.paperHi, border: `1.2px solid ${M.muteLo}` }}>
            <div style={{ width: 28, height: 28, background: r.hue, color: M.paperHi, fontFamily: M.fDisplay, fontWeight: 800, fontSize: 11, display: "flex", alignItems: "center", justifyContent: "center", border: `1.2px solid ${M.ink}` }}>{r.i}</div>
            <div style={{ flex: 1 }}>
              <div style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 13 }}>{r.n}</div>
              <div style={{ fontFamily: M.fDisplay, fontSize: 11.5, color: M.mute }}>{r.why}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{ marginTop: 14, padding: 10, fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, color: M.mute, textTransform: "uppercase", textAlign: "center", lineHeight: 1.5, borderTop: `1px solid ${M.muteLo}` }}>
        tidak ada peringkat kompetitif.<br/>setiap audit dihitung sama.
      </div>
    </div>
    <BottomTabs active="profil"/>
  </Phone>
);

// ============================================================
// Bottom Tabs — shared chrome
// ============================================================
const BottomTabs = ({ active }) => (
  <div style={{ position: "absolute", bottom: 0, left: 0, right: 0, height: 68, background: M.paperHi, borderTop: `1.5px solid ${M.ink}`, display: "flex", justifyContent: "space-around", alignItems: "center", paddingBottom: 8 }}>
    {[
      { id: "kamera",   ic: "shutter", l: "Audit" },
      { id: "linimasa", ic: "grid",    l: "Linimasa" },
      { id: "peta",     ic: "loc",     l: "Peta" },
      { id: "profil",   ic: "info",    l: "Profil" },
    ].map(t => (
      <div key={t.id} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 2, color: active === t.id ? M.ink : M.mute }}>
        <Ic n={t.ic} s={20} w={active === t.id ? 2.2 : 1.6}/>
        <div style={{ fontFamily: M.fMono, fontSize: 9, letterSpacing: 1.2, fontWeight: active === t.id ? 700 : 500, textTransform: "uppercase" }}>{t.l}</div>
        {active === t.id && <div style={{ width: 16, height: 2, background: M.ink, marginTop: 1 }}/>}
      </div>
    ))}
  </div>
);

Object.assign(window, { ScreenFeed, ScreenMap, ScreenProfile, ScreenBadges });

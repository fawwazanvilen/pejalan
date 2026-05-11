// Mid-fi screens — multiple flow states, single canvas.

// ============================================================
// 1. PRE-CAPTURE — viewfinder live, aiming the camera
// ============================================================
const ScreenPreCapture = () => (
  <Phone label="01" sub="Pre-capture">
    <CarPhoto h={844} showBbox={false} scrim={false} dim={0.18} blur={0}/>
    {/* reticle */}
    <svg width="100%" height="100%" viewBox="0 0 390 844" preserveAspectRatio="none"
      style={{ position: "absolute", inset: 0, pointerEvents: "none" }}>
      <g stroke={M.hivis} strokeWidth="2.5" fill="none" opacity="0.9">
        <path d="M 30 60 L 30 30 L 60 30"/>
        <path d="M 360 60 L 360 30 L 330 30"/>
        <path d="M 30 700 L 30 730 L 60 730"/>
        <path d="M 360 700 L 360 730 L 330 730"/>
      </g>
      {/* center crosshair */}
      <g stroke="rgba(255,255,255,0.7)" strokeWidth="1.2" fill="none">
        <line x1="195" y1="370" x2="195" y2="390"/>
        <line x1="185" y1="380" x2="205" y2="380"/>
      </g>
    </svg>
    <PhotoChrome showAnalyze={false} frameLabel="HORIZONTAL · BIDIK TROTOAR"/>
    {/* hint */}
    <div style={{ position: "absolute", top: 88, left: "50%", transform: "translateX(-50%)", zIndex: 5,
      padding: "8px 14px", background: "rgba(0,0,0,0.65)", color: "#fff",
      fontFamily: M.fDisplay, fontSize: 14, fontWeight: 600, letterSpacing: 0.2, textAlign: "center", maxWidth: 320 }}>
      Arahkan ke trotoar yang bermasalah.<br/>
      <span style={{ fontFamily: M.fMono, fontSize: 11, opacity: 0.7, letterSpacing: 1.2 }}>JARAK 1-3 METER · CAHAYA CUKUP</span>
    </div>
    {/* shutter bar */}
    <div style={{ position: "absolute", bottom: 0, left: 0, right: 0, height: 130, background: "linear-gradient(180deg, rgba(0,0,0,0), rgba(0,0,0,0.7))", display: "flex", alignItems: "center", justifyContent: "space-around", paddingBottom: 28 }}>
      <div style={{ width: 44, height: 44, border: `1.5px solid #fff`, display: "flex", alignItems: "center", justifyContent: "center", color: "#fff" }}>
        <Ic n="grid" s={20}/>
      </div>
      <div style={{ width: 80, height: 80, borderRadius: "50%", border: `4px solid #fff`, display: "flex", alignItems: "center", justifyContent: "center" }}>
        <div style={{ width: 62, height: 62, borderRadius: "50%", background: "#fff" }}/>
      </div>
      <div style={{ width: 44, height: 44, border: `1.5px solid #fff`, display: "flex", alignItems: "center", justifyContent: "center", color: "#fff" }}>
        <Ic n="loc" s={20}/>
      </div>
    </div>
  </Phone>
);

// ============================================================
// 2. ANALYZING — just snapped, model running
// ============================================================
const ScreenAnalyzing = () => (
  <Phone label="02" sub="Analyzing (≈0.4s in)">
    <CarPhoto h={844} showBbox={false} scrim dim={0.35} blur={0.4}/>
    <PhotoChrome frameLabel="ANALISIS BERJALAN…" customMarker={<><span style={{ width: 6, height: 6, background: M.hivis, animation: "blink 0.6s infinite" }}/> GEMMA 4 · ON-DEVICE</>}/>
    <div style={{ position: "absolute", top: "38%", left: 28, right: 28, zIndex: 5 }}>
      {/* big mono progress strip */}
      <div style={{ fontFamily: M.fMono, fontSize: 11, color: "#fff", opacity: 0.7, letterSpacing: 2, marginBottom: 10 }}>
        STEP 02 / 04 — KLASIFIKASI
      </div>
      <div style={{ fontFamily: M.fDisplay, fontSize: 34, fontWeight: 700, color: "#fff", lineHeight: 1.05, letterSpacing: -0.5 }}>
        Membaca trotoar.<br/>
        <span style={{ color: M.hivis }}>0.4 detik…</span>
      </div>
      <div style={{ marginTop: 18, height: 6, background: "rgba(255,255,255,0.18)", overflow: "hidden" }}>
        <div style={{ width: "62%", height: "100%", background: M.hivis }}/>
      </div>
      <div style={{ marginTop: 14, fontFamily: M.fMono, fontSize: 10.5, color: "#fff", letterSpacing: 1.2, opacity: 0.7, lineHeight: 1.7 }}>
        ✓ DETEKSI OBJEK<br/>
        → KLASIFIKASI KATEGORI<br/>
        ◯ PENILAIAN SEVERITAS<br/>
        ◯ MENULIS RASIONAL
      </div>
    </div>
  </Phone>
);

// ============================================================
// 3. HIGH-CONF RESULT (refreshed visual)
// ============================================================
const ScreenA = () => {
  const vH = 350;
  return (
    <Phone label="03" sub="Result · high confidence">
      <CarPhoto h={vH}/>
      <PhotoChrome/>
      <Sheet top={vH} accent={M.ox}>
        <AuditCode code="PJ-024-0247" time="09:42 WIB"/>

        {/* CATEGORY HERO — large display type */}
        <FieldLabel n="01">Kategori terdeteksi</FieldLabel>
        <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", gap: 12, marginTop: 4 }}>
          <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 36, lineHeight: 0.95, letterSpacing: -1.2, color: M.ink, textTransform: "lowercase" }}>
            parkir<br/>liar.
          </div>
          <SevStamp level="tinggi" rot={-2}/>
        </div>

        {/* CONFIDENCE */}
        <div style={{ marginTop: 16 }}>
          <ConfBlocks level={5} label="sangat yakin" c={M.teal}/>
        </div>

        {/* RATIONALE */}
        <div style={{ marginTop: 14, paddingTop: 12, borderTop: `1.5px solid ${M.ink}` }}>
          <FieldLabel n="02">Apa yang dilihat</FieldLabel>
          <div style={{ marginTop: 6, fontFamily: M.fDisplay, fontSize: 15, lineHeight: 1.4, color: M.ink, fontWeight: 500 }}>
            Trotoar tersumbat penuh oleh mobil parkir. Pejalan kaki harus turun ke jalan raya.
          </div>
        </div>

        {/* CHIPS — sharp, low-emphasis */}
        <div style={{ marginTop: 14, paddingTop: 12, borderTop: `1.5px solid ${M.ink}` }}>
          <FieldLabel n="03">Bukan ini? Ganti kategori</FieldLabel>
          <div style={{ display: "flex", gap: 6, flexWrap: "wrap", marginTop: 8 }}>
            {["halangan permanen", "trotoar rusak", "lainnya"].map(t => (
              <button key={t} style={{
                padding: "7px 12px", background: M.paperHi, border: `1.4px solid ${M.ink}`, color: M.ink,
                fontFamily: M.fDisplay, fontSize: 13, fontWeight: 600, cursor: "pointer", whiteSpace: "nowrap",
              }}>{t}</button>
            ))}
          </div>
        </div>

        <div style={{ flex: 1 }}/>

        {/* FOOTER */}
        <div style={{ display: "flex", gap: 8, marginTop: 14 }}>
          <SqBtn icon="mic" size={52}/>
          <div style={{ flex: 1 }}><Btn tone="ink" icon="arr">Lanjutkan</Btn></div>
        </div>
        <button style={{ marginTop: 8, background: "none", border: "none", cursor: "pointer", color: M.mute, fontFamily: M.fMono, fontSize: 11, letterSpacing: 1.4, textTransform: "uppercase", padding: 6, alignSelf: "center" }}>
          Lihat detail penalaran →
        </button>
      </Sheet>
    </Phone>
  );
};

// ============================================================
// 4. MED-CONF RESULT — chips emphasized
// ============================================================
const ScreenB = () => {
  const vH = 260;
  return (
    <Phone label="04" sub="Result · medium confidence">
      <CarPhoto h={vH} bboxLabel="pola ubin tidak jelas"/>
      <PhotoChrome frameLabel="FOTO #2 / 47 HARI INI"/>
      <Sheet top={vH} accent={M.amber}>
        <AuditCode code="PJ-024-0248"/>

        {/* AGAK RAGU banner */}
        <div style={{ background: M.amberTint, border: `1.6px solid ${M.amber}`, padding: "8px 10px", display: "flex", gap: 8, alignItems: "flex-start" }}>
          <Ic n="info" s={16} c={M.amber} w={2}/>
          <div style={{ fontFamily: M.fDisplay, fontSize: 13, lineHeight: 1.35, color: M.ink }}>
            <b style={{ color: M.amber }}>Agak ragu.</b> Mohon pilih kategori yang benar di bawah.
          </div>
        </div>

        {/* SUMMARY */}
        <div style={{ marginTop: 12 }}>
          <FieldLabel n="01">Dugaan AI · kemungkinan tertinggi</FieldLabel>
          <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", gap: 10, marginTop: 4 }}>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 24, lineHeight: 1, letterSpacing: -0.5, color: M.ink, textTransform: "lowercase" }}>
              ubin difabel<br/>bermasalah.
            </div>
            <SevStamp level="sedang" rot={-1.5}/>
          </div>
          <div style={{ marginTop: 10 }}>
            <ConfBlocks level={3} label="cukup yakin" c={M.amber}/>
          </div>
        </div>

        {/* RATIONALE — compact */}
        <div style={{ marginTop: 10, fontFamily: M.fDisplay, fontSize: 13.5, lineHeight: 1.4, color: M.ink, paddingLeft: 10, borderLeft: `2px solid ${M.amber}` }}>
          Pola ubin pemandu tidak konsisten — penyandang netra bisa kehilangan jalur.
        </div>

        {/* CORRECTION LIST — the hero */}
        <div style={{ marginTop: 12, paddingTop: 10, borderTop: `1.5px solid ${M.ink}` }}>
          <FieldLabel n="02">Apa sebenarnya yang terlihat?</FieldLabel>
          <div style={{ display: "flex", flexDirection: "column", gap: 4, marginTop: 8 }}>
            {[
              { ic: "tiles", l: "Ubin difabel bermasalah", s: 0.64, sel: true },
              { ic: "crack", l: "Trotoar rusak",             s: 0.21 },
              { ic: "drain", l: "Drainase / lubang",         s: 0.09 },
            ].map((r, i) => (
              <button key={i} style={{
                width: "100%", padding: "8px 10px",
                background: r.sel ? M.tealTint : M.paperHi,
                border: r.sel ? `2px solid ${M.teal}` : `1.4px solid ${M.muteLo}`,
                cursor: "pointer", display: "flex", alignItems: "center", gap: 10, textAlign: "left",
              }}>
                <Ic n={r.ic} s={20} c={r.sel ? M.tealInk : M.ink} w={1.8}/>
                <span style={{ flex: 1, fontFamily: M.fDisplay, fontWeight: r.sel ? 700 : 500, fontSize: 14, color: M.ink }}>{r.l}</span>
                <span style={{ fontFamily: M.fMono, fontSize: 11, fontWeight: 700, color: r.sel ? M.tealInk : M.mute, letterSpacing: 0.4 }}>{Math.round(r.s*100)}</span>
              </button>
            ))}
            <button style={{ padding: "6px 10px", background: "none", border: "none", cursor: "pointer", color: M.mute, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, textTransform: "uppercase", textAlign: "left" }}>
              + 3 kategori lain
            </button>
          </div>
        </div>

        <div style={{ flex: 1, minHeight: 4 }}/>

        <div style={{ display: "flex", gap: 8, marginTop: 8 }}>
          <SqBtn icon="mic" size={52}/>
          <div style={{ flex: 1 }}><Btn tone="ink" icon="arr">Konfirmasi & lanjut</Btn></div>
        </div>
      </Sheet>
    </Phone>
  );
};

// ============================================================
// 5. VOICE RECORDING state
// ============================================================
const ScreenVoice = () => {
  const vH = 230;
  return (
    <Phone label="05" sub="Voice memo · recording">
      <CarPhoto h={vH} blur={1.5} dim={0.3}/>
      <PhotoChrome frameLabel="REC · TAMBAH KONTEKS"/>
      <Sheet top={vH} accent={M.ox}>
        <AuditCode code="PJ-024-0247 · MEMO"/>
        <FieldLabel n="04">Catatan suara</FieldLabel>
        <div style={{ fontFamily: M.fDisplay, fontSize: 22, fontWeight: 700, lineHeight: 1.15, marginTop: 6, color: M.ink, textTransform: "lowercase" }}>
          ceritakan apa yang AI tidak bisa lihat.
        </div>

        {/* waveform */}
        <div style={{ marginTop: 18, height: 110, background: M.paperHi, border: `1.5px solid ${M.ink}`, position: "relative", overflow: "hidden" }}>
          <svg viewBox="0 0 320 110" width="100%" height="100%" preserveAspectRatio="none">
            {Array.from({length: 48}).map((_, i) => {
              const h = 16 + Math.abs(Math.sin(i*0.7) * 38) + (i < 24 ? 0 : Math.random() * 18);
              const active = i < 30;
              return <rect key={i} x={6 + i*6.6} y={(110-h)/2} width="3.6" height={h} fill={active ? M.ox : M.muteLo}/>;
            })}
          </svg>
          <div style={{ position: "absolute", bottom: 4, left: 8, fontFamily: M.fMono, fontSize: 10, color: M.mute, letterSpacing: 1 }}>00:18 / 00:30</div>
          <div style={{ position: "absolute", top: 6, right: 8, display: "flex", alignItems: "center", gap: 4, fontFamily: M.fMono, fontSize: 10, color: M.ox, letterSpacing: 1.2, fontWeight: 700 }}>
            <span style={{ width: 8, height: 8, background: M.ox }}/>REC
          </div>
        </div>

        <div style={{ marginTop: 12, fontFamily: M.fMono, fontSize: 10.5, color: M.mute, lineHeight: 1.6, letterSpacing: 0.6 }}>
          TIP — sebutkan waktu, kerumunan, atau kebiasaan sehari-hari yang berulang di lokasi ini.
        </div>

        <div style={{ flex: 1 }}/>

        {/* footer: stop + cancel */}
        <div style={{ display: "flex", gap: 8 }}>
          <div style={{ flex: 1 }}><Btn tone="ox" icon="stop">Selesai merekam</Btn></div>
        </div>
        <button style={{ marginTop: 8, alignSelf: "center", padding: 6, background: "none", border: "none", cursor: "pointer", color: M.mute, fontFamily: M.fMono, fontSize: 11, letterSpacing: 1.4, textTransform: "uppercase" }}>
          Batal & hapus
        </button>
      </Sheet>
    </Phone>
  );
};

// ============================================================
// 6. DETAIL PENALARAN — full sheet, AI breakdown
// ============================================================
const ScreenDetail = () => (
  <Phone label="06" sub="Detail penalaran · full">
    <div style={{ position: "absolute", inset: 0, background: M.paper, color: M.ink, padding: "44px 22px 22px", overflow: "hidden" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <button style={{ background: "none", border: "none", padding: 0, color: M.ink, display: "inline-flex", alignItems: "center", gap: 6, fontFamily: M.fDisplay, fontWeight: 600, fontSize: 14 }}>
          <Ic n="back" s={20}/> Kembali
        </button>
        <span style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute }}>DETAIL · PJ-024-0247</span>
      </div>

      <div style={{ marginTop: 22, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.6, color: M.mute, textTransform: "uppercase" }}>Penalaran AI</div>
      <div style={{ marginTop: 4, fontFamily: M.fDisplay, fontWeight: 800, fontSize: 30, lineHeight: 1, letterSpacing: -0.8, textTransform: "lowercase" }}>
        kenapa parkir liar?
      </div>

      {/* mini thumb */}
      <div style={{ marginTop: 14, position: "relative", height: 120, border: `1.5px solid ${M.ink}`, overflow: "hidden" }}>
        <img src={PHOTO} style={{ position: "absolute", inset: 0, width: "100%", height: "100%", objectFit: "cover", objectPosition: "center 22%" }} alt=""/>
        <svg width="100%" height="100%" viewBox="0 0 390 120" preserveAspectRatio="none" style={{ position: "absolute", inset: 0 }}>
          <CarBbox h={120} label="" color={M.hivis}/>
        </svg>
      </div>

      {/* reasoning list */}
      <div style={{ marginTop: 16, display: "flex", flexDirection: "column", gap: 12 }}>
        {[
          { n: "01", t: "Objek terdeteksi", b: "Mobil sedan menutupi ~95% lebar trotoar; pelat berwarna hitam." },
          { n: "02", t: "Konteks ruang", b: "Permukaan ubin paving khas trotoar pejalan kaki, bukan parkir resmi." },
          { n: "03", t: "Dampak pejalan", b: "Sisa lebar 0.0m — penyandang kursi roda atau orang dengan anak tidak bisa lewat." },
          { n: "04", t: "Severitas — tinggi", b: "Trotoar tertutup penuh; pejalan terpaksa berbagi badan jalan dengan motor." },
        ].map(r => (
          <div key={r.n} style={{ display: "flex", gap: 12 }}>
            <div style={{ fontFamily: M.fMono, fontSize: 11, color: M.mute, letterSpacing: 1.2, paddingTop: 2 }}>{r.n}</div>
            <div>
              <div style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 14, color: M.ink }}>{r.t}</div>
              <div style={{ fontFamily: M.fDisplay, fontSize: 13, lineHeight: 1.4, color: M.inkSoft, marginTop: 2 }}>{r.b}</div>
            </div>
          </div>
        ))}
      </div>

      <div style={{ marginTop: "auto", position: "absolute", bottom: 22, left: 22, right: 22, fontFamily: M.fMono, fontSize: 10, color: M.muteLo, letterSpacing: 1, lineHeight: 1.5 }}>
        DIBUAT DI PERANGKAT · GEMMA 4 · 0.8s<br/>
        TIDAK DIKIRIM KE SERVER TANPA IZIN.
      </div>
    </div>
  </Phone>
);

// ============================================================
// 7. MANUAL CATEGORY PICKER — when AI was wrong
// ============================================================
const ScreenPicker = () => {
  const cats = [
    { ic: "car",    l: "Parkir liar",            d: "Kendaraan di trotoar." },
    { ic: "crack",  l: "Trotoar rusak",          d: "Retak, lubang, pecah." },
    { ic: "pole",   l: "Halangan permanen",      d: "Tiang, pohon, gerobak." },
    { ic: "tiles",  l: "Ubin difabel bermasalah",d: "Pola pemandu hilang." },
    { ic: "noside", l: "Trotoar absen",          d: "Tidak ada trotoar." },
    { ic: "drain",  l: "Drainase",               d: "Got terbuka, manhole hilang." },
  ];
  return (
    <Phone label="07" sub="Manual category picker">
      <div style={{ position: "absolute", inset: 0, background: M.paper, color: M.ink, padding: "44px 18px 18px", overflow: "hidden" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <button style={{ background: "none", border: "none", padding: 0, color: M.ink, display: "inline-flex", alignItems: "center", gap: 6, fontFamily: M.fDisplay, fontWeight: 600, fontSize: 14 }}>
            <Ic n="back" s={20}/> Kembali
          </button>
          <span style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.4, color: M.mute }}>UBAH KATEGORI</span>
        </div>

        <div style={{ marginTop: 16, fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.6, color: M.mute, textTransform: "uppercase" }}>Pilih sendiri</div>
        <div style={{ marginTop: 2, fontFamily: M.fDisplay, fontWeight: 800, fontSize: 28, lineHeight: 1, letterSpacing: -0.6, textTransform: "lowercase" }}>
          apa yang kamu lihat?
        </div>

        <div style={{ marginTop: 18, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
          {cats.map((c, i) => (
            <button key={i} style={{
              padding: "12px 10px", background: M.paperHi, border: `1.5px solid ${M.ink}`,
              cursor: "pointer", textAlign: "left", display: "flex", flexDirection: "column", gap: 10,
              minHeight: 110,
            }}>
              <Ic n={c.ic} s={26} w={1.8}/>
              <div style={{ fontFamily: M.fDisplay, fontWeight: 700, fontSize: 13.5, lineHeight: 1.15 }}>{c.l}</div>
              <div style={{ fontFamily: M.fDisplay, fontSize: 11.5, color: M.mute, lineHeight: 1.3, marginTop: "auto" }}>{c.d}</div>
            </button>
          ))}
        </div>

        <button style={{ marginTop: 14, width: "100%", padding: "12px 14px", background: "none", border: `1.5px dashed ${M.mute}`, cursor: "pointer", fontFamily: M.fDisplay, fontWeight: 600, fontSize: 13.5, color: M.mute, textAlign: "left", display: "flex", alignItems: "center", gap: 10 }}>
          <Ic n="edit" s={18} c={M.mute}/> Lainnya — ketik kategori baru
        </button>
      </div>
    </Phone>
  );
};

// ============================================================
// 8. SAVED — confirmation, ready to keep walking
// ============================================================
const ScreenSaved = () => (
  <Phone label="08" sub="Saved · keep walking">
    <div style={{ position: "absolute", inset: 0, background: M.paper, color: M.ink, padding: "44px 22px 22px", overflow: "hidden", display: "flex", flexDirection: "column" }}>
      <div style={{ fontFamily: M.fMono, fontSize: 10.5, letterSpacing: 1.6, color: M.mute }}>AUDIT TERSIMPAN · PJ-024-0247</div>

      <div style={{ marginTop: 28 }}>
        <div style={{ display: "inline-block", padding: "6px 12px", background: M.teal, color: M.paperHi, fontFamily: M.fMono, fontSize: 11, fontWeight: 700, letterSpacing: 1.8, transform: "rotate(-1.5deg)" }}>
          ✓ TERSIMPAN OFFLINE
        </div>
      </div>

      <div style={{ marginTop: 16, fontFamily: M.fDisplay, fontWeight: 800, fontSize: 44, lineHeight: 0.95, letterSpacing: -1.3, textTransform: "lowercase" }}>
        terima kasih.<br/>
        <span style={{ color: M.teal }}>lanjut menelusuri.</span>
      </div>

      <div style={{ marginTop: 22, padding: "12px 14px", background: M.paperHi, border: `1.5px solid ${M.ink}` }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", fontFamily: M.fMono, fontSize: 11, letterSpacing: 1.4, color: M.mute, textTransform: "uppercase" }}>
          <span>Hari ini</span><span>JL. Kebon Sirih</span>
        </div>
        <div style={{ display: "flex", gap: 18, marginTop: 8, alignItems: "baseline" }}>
          <div>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 38, lineHeight: 0.9 }}>47</div>
            <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, color: M.mute, marginTop: 4 }}>FOTO</div>
          </div>
          <div>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 38, lineHeight: 0.9 }}>1.8</div>
            <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, color: M.mute, marginTop: 4 }}>KM</div>
          </div>
          <div>
            <div style={{ fontFamily: M.fDisplay, fontWeight: 800, fontSize: 38, lineHeight: 0.9, color: M.ox }}>12</div>
            <div style={{ fontFamily: M.fMono, fontSize: 10, letterSpacing: 1.2, color: M.mute, marginTop: 4 }}>SEVERITAS&nbsp;TINGGI</div>
          </div>
        </div>
      </div>

      <div style={{ flex: 1 }}/>

      <Btn tone="teal" icon="arr" h={56}>Lanjut ke kamera</Btn>
      <button style={{ marginTop: 10, padding: 8, background: "none", border: "none", cursor: "pointer", color: M.mute, fontFamily: M.fMono, fontSize: 11, letterSpacing: 1.4, textTransform: "uppercase", alignSelf: "center" }}>
        Selesai untuk hari ini →
      </button>
    </div>
  </Phone>
);

Object.assign(window, { ScreenPreCapture, ScreenAnalyzing, ScreenA, ScreenB, ScreenVoice, ScreenDetail, ScreenPicker, ScreenSaved });


// ============ DIRECTIONS (merged) ============
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


// ============ COMMUNITY (merged) ============
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

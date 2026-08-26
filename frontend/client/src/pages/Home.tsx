// Blue Arc style: one deterministic anime intro overlay, stable poster fallback, and video-only download CTA.
import { useCallback, useEffect, useRef, useState } from "react";
import { ArrowRight, Download, Menu, Play, Sparkles, Waves, X } from "lucide-react";

const videoUrl = "/videos/blue-whale-demo.mp4";
const posterUrl = "/videos/blue-whale-intro.mp4";
const introDuration = 8000;

const cards = [
  { number: "01", title: "Ocean Explorer", copy: "Explore the bright ocean,swim through coraal reefs,and learn how to control the blue whale." },
  { number: "02", title: "Deep Sea Adventure", copy: "Dive deeper into the ocean and navigate around rocks and underwater obstacles and exploring the deep sea." },
  { number: "03", title: "CraftLand Challenge", copy: "Reach the darkest part of the ocean and complete the final challenge and find your way back to the surface." },
];

const archiveRows = [["FORMAT","Adventurous"], ["DURATION", "10-20 Minutes"], ["MOOD", "Ocean night"], ["GAMEPLAY", "Explore & Survive"]];

function restartAnimation(node: HTMLElement | null) {
  if (!node) return;
  node.classList.remove("intro-restart");
  void node.offsetWidth;
  node.classList.add("intro-restart");
}

export default function Home() {
  const [introVisible, setIntroVisible] = useState(true);
  const [videoReady, setVideoReady] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const introRef = useRef<HTMLDivElement>(null);
  const introVideoRef = useRef<HTMLVideoElement>(null);
  const introTimer = useRef<number | null>(null);
  const lastReplay = useRef(0);
  const introCompleted = useRef(false);

  const replayIntro = useCallback(() => {
    if (introCompleted.current) return;
    const now = Date.now();
    if (now - lastReplay.current < 700) return;
    lastReplay.current = now;
    setIntroVisible(true);
    setVideoReady(false);
    if (introTimer.current) window.clearTimeout(introTimer.current);
    introTimer.current = window.setTimeout(() => {
      introCompleted.current = true;
      setIntroVisible(false);
    }, introDuration);
    restartAnimation(introRef.current);
    const video = introVideoRef.current;
    if (video) {
      video.pause();
      video.currentTime = 0;
      video.load();
      void video.play().catch(() => undefined);
    }
  }, []);

  useEffect(() => {
    replayIntro();
    const onPageShow = (event: PageTransitionEvent) => {
      if (event.persisted) {
        introCompleted.current = false;
        replayIntro();
      }
    };
    const onVisibility = () => {
      if (document.visibilityState === "visible" && !introCompleted.current) replayIntro();
    };
    const onFocus = () => {
      if (document.visibilityState === "visible" && !introCompleted.current) replayIntro();
    };
    window.addEventListener("pageshow", onPageShow);
    document.addEventListener("visibilitychange", onVisibility);
    window.addEventListener("focus", onFocus);
    return () => {
      if (introTimer.current) window.clearTimeout(introTimer.current);
      window.removeEventListener("pageshow", onPageShow);
      document.removeEventListener("visibilitychange", onVisibility);
      window.removeEventListener("focus", onFocus);
    };
  }, [replayIntro]);

  const skipIntro = () => {
    introCompleted.current = true;
    if (introTimer.current) window.clearTimeout(introTimer.current);
    setIntroVisible(false);
    introVideoRef.current?.pause();
  };

  return (
    <div className="blue-arc-page">
      <div ref={introRef} className={`intro-screen ${introVisible ? "intro-visible" : "intro-hidden"}`} aria-hidden={!introVisible}>
        <div className="intro-grid" aria-hidden="true" /><div className="intro-moon" aria-hidden="true" /><div className="intro-wave intro-wave-one" aria-hidden="true" />
        <div className="intro-copy"><p className="intro-kicker"><Sparkles size={14} /> WELCOME TO</p><h1>Blue<br /><span>Whale</span></h1></div>
        <div className="intro-video-orbit"><img className={`intro-poster ${videoReady ? "poster-hidden" : ""}`} src={posterUrl} alt="" aria-hidden="true" /><video ref={introVideoRef} muted playsInline preload="auto" aria-label="Opening Blue Whale video" onLoadedData={(event) => { setVideoReady(true); void event.currentTarget.play().catch(() => undefined); }} onPlaying={() => setVideoReady(true)} onError={() => setVideoReady(false)}><source src={posterUrl} type="video/mp4" /></video></div>
        <button className="skip-button" onClick={skipIntro}>Skip Intro <ArrowRight size={15} /></button>
      </div>

      <header className="arc-header"><a className="arc-logo" href="#home"><span className="arc-logo-mark"><Waves size={20} /></span><span><strong>BLUE WHALE GAME</strong><small>DOWNLOAD FOR FREE</small></span></a><nav className={menuOpen ? "arc-nav nav-open" : "arc-nav"}><a href="#home" onClick={() => setMenuOpen(false)}>Home</a><a href="#chapters" onClick={() => setMenuOpen(false)}>Chapters</a><a href="#archive" onClick={() => setMenuOpen(false)}>Archive</a><a className="nav-download" href={videoUrl} download="blue-whale-video.mp4" onClick={() => setMenuOpen(false)}><Download size={14} /> Download Blue Whale</a></nav><button className="menu-toggle" onClick={() => setMenuOpen(!menuOpen)} aria-label="Toggle navigation">{menuOpen ? <X /> : <Menu />}</button></header>

      <main>
        <section id="home" className="arc-hero"><div className="hero-curve curve-a" aria-hidden="true" /><div className="hero-copy"><p className="section-kicker"><span /> SEASON 1 / Ocean Signal</p><h2>Ride The<br /><em>Blue Whale.</em></h2><p className="hero-description">The Best Adventurous Real-World Game.The GamePlay is Smooth and the Controls are easy to learn.Playing with friends makes the experience even more exciting .</p><div className="hero-buttons"><a className="arc-button arc-button-coral" href={videoUrl} download="blue-whale-video.mp4"><Download size={16} /> Download Blue Whale</a><a className="arc-text-link" href="#chapters"><Play size={15} fill="currentColor" /> Explore the page</a></div></div><div className="hero-video-card"><div className="card-topline"><span>NOW PLAYING</span><span>00:09</span></div><div className="hero-video-frame"><video controls playsInline preload="metadata" poster={posterUrl} aria-label="Blue Whale video"><source src={videoUrl} type="video/mp4" /></video><div className="frame-tag">BLUE / 01</div></div><p className="video-caption">Watch the Demo</p></div></section>
        <section id="chapters" className="arc-section chapter-section"><div className="section-heading"><p className="section-kicker"><span /> THE BLUE ARC</p><h2>Multi-Modes<br /><em>In The Game.</em></h2></div><div className="arc-cards">{cards.map((card) => <article className="arc-card" key={card.number}><span className="card-number">{card.number}</span><div className="card-spark">✦</div><h3>{card.title}</h3><p>{card.copy}</p><a href="#archive" aria-label={`View ${card.title}`}>View signal <ArrowRight size={15} /></a></article>)}</div></section>
        <section id="archive" className="arc-section archive-section"><div className="archive-intro"><p className="section-kicker"><span /> ABOUT</p><h2>WHALE ADVENTURES.<br /><em>NEW EVENTS.</em></h2><p>The Main Aim is to design a Virtual-World and Play Moderately . Made for the ones who listen to the deep mindset and feels better wherever the tide takes you.</p></div><div className="archive-table">{archiveRows.map(([label, value]) => <div className="archive-row" key={label}><span>{label}</span><strong>{value}</strong></div>)}<a className="arc-button arc-button-outline" href={videoUrl} download="blue-whale-video.mp4"><Download size={16} /> Download Blue Whale</a></div></section>
      </main>
      <footer className="arc-footer"><div className="arc-logo"><span className="arc-logo-mark"><Waves size={20} /></span><span><strong>BLUE WHALE GAME</strong><small>BLUE ARC STUDIO</small></span></div><p>© 2026 Blue Arc Studio.</p><a href="#home">Back to top ↑</a></footer>
    </div>
  );
}

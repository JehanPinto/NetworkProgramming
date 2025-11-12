
'use client';

import { useState } from "react";

const MODES = {
  LOGIN: "LOGIN",
  ADMIN: "ADMIN",
  USER: "USER",
};

export default function Home() {
  const [mode, setMode] = useState(MODES.LOGIN);
  const [showAdminLogin, setShowAdminLogin] = useState(false);
  const [adminError, setAdminError] = useState("");

  // Placeholder handlers for demo UI
  const handleAdminLogin = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: Connect to backend
    setAdminError("");
    setMode(MODES.ADMIN);
  };
  const handleUserEnter = () => setMode(MODES.USER);
  const handleBack = () => {
    setShowAdminLogin(false);
    setAdminError("");
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-[#0f0f23] to-[#1a1a2e] p-4">
      <div className="w-full max-w-2xl rounded-2xl shadow-2xl bg-[#18182f] overflow-hidden">
        {/* Login Screen */}
        {mode === MODES.LOGIN && (
          <div className="p-8">
            <header className="mb-8 text-center">
              <h1 className="text-4xl font-bold bg-gradient-to-r from-cyan-400 to-pink-400 bg-clip-text text-transparent mb-2">
                🏛️ Auction House
              </h1>
              <p className="text-zinc-400">Select your role to continue</p>
            </header>
            <div className="flex flex-col items-center gap-8">
              {!showAdminLogin ? (
                <div className="flex gap-8 flex-wrap justify-center">
                  <button
                    className="w-40 h-40 flex flex-col items-center justify-center rounded-xl border-2 border-zinc-700 bg-[#23234a] hover:border-cyan-400 transition group"
                    onClick={() => setShowAdminLogin(true)}
                  >
                    <span className="text-5xl mb-2">👨‍💼</span>
                    <span className="font-semibold text-lg group-hover:text-cyan-400">Admin</span>
                  </button>
                  <button
                    className="w-40 h-40 flex flex-col items-center justify-center rounded-xl border-2 border-zinc-700 bg-[#23234a] hover:border-pink-400 transition group"
                    onClick={handleUserEnter}
                  >
                    <span className="text-5xl mb-2">👤</span>
                    <span className="font-semibold text-lg group-hover:text-pink-400">Bidder</span>
                  </button>
                </div>
              ) : (
                <form
                  className="w-full max-w-xs mx-auto flex flex-col gap-4"
                  onSubmit={handleAdminLogin}
                >
                  <h2 className="text-center text-2xl font-bold text-cyan-400 mb-2">Admin Login</h2>
                  <input
                    className="rounded-lg px-4 py-2 bg-[#23234a] border border-zinc-700 text-zinc-100 focus:outline-none focus:border-cyan-400"
                    type="text"
                    placeholder="Username"
                    required
                  />
                  <input
                    className="rounded-lg px-4 py-2 bg-[#23234a] border border-zinc-700 text-zinc-100 focus:outline-none focus:border-cyan-400"
                    type="password"
                    placeholder="Password"
                    required
                  />
                  {adminError && (
                    <div className="text-pink-400 text-center bg-pink-400/10 rounded-lg py-1">
                      {adminError}
                    </div>
                  )}
                  <button
                    type="submit"
                    className="rounded-lg py-2 font-semibold bg-gradient-to-r from-cyan-400 to-blue-500 text-white hover:from-cyan-300 hover:to-blue-400 transition"
                  >
                    Login
                  </button>
                  <button
                    type="button"
                    className="rounded-lg py-2 font-semibold bg-[#23234a] border border-zinc-700 text-zinc-200 hover:bg-zinc-800 transition"
                    onClick={handleBack}
                  >
                    Back
                  </button>
                </form>
              )}
            </div>
          </div>
        )}

        {/* Admin Dashboard (Demo UI) */}
        {mode === MODES.ADMIN && (
          <div className="p-8">
            <header className="mb-8 text-center">
              <h1 className="text-3xl font-bold text-cyan-400 mb-2">Admin Dashboard</h1>
              <p className="text-green-400">Connected</p>
            </header>
            <div className="grid gap-8 md:grid-cols-2">
              {/* Auction Control Panel */}
              <div className="bg-[#23234a] rounded-xl p-6 border border-zinc-700">
                <h2 className="text-xl font-bold text-cyan-400 mb-4">Auction Control</h2>
                <div className="flex flex-col gap-4">
                  <label className="text-zinc-300">Starting Price ($)</label>
                  <input
                    className="rounded-lg px-4 py-2 bg-[#18182f] border border-zinc-700 text-zinc-100 focus:outline-none focus:border-cyan-400"
                    type="number"
                    min={1}
                    step={0.01}
                    defaultValue={50}
                  />
                  <label className="text-zinc-300">Duration (seconds)</label>
                  <input
                    className="rounded-lg px-4 py-2 bg-[#18182f] border border-zinc-700 text-zinc-100 focus:outline-none focus:border-cyan-400"
                    type="number"
                    min={10}
                    max={600}
                    defaultValue={60}
                  />
                  <button className="rounded-lg py-2 font-semibold bg-gradient-to-r from-cyan-400 to-blue-500 text-white hover:from-cyan-300 hover:to-blue-400 transition mt-2">
                    Start Auction
                  </button>
                  <div className="flex items-center gap-2 mt-2">
                    <span className="text-zinc-400">Time Remaining:</span>
                    <span className="font-mono text-lg text-cyan-300">--:--</span>
                  </div>
                </div>
              </div>
              {/* Bid Details */}
              <div className="bg-[#23234a] rounded-xl p-6 border border-zinc-700 flex flex-col gap-4">
                <h2 className="text-xl font-bold text-cyan-400 mb-2">Current Auction Status</h2>
                <div className="grid grid-cols-1 gap-2">
                  <div className="flex justify-between">
                    <span className="text-zinc-400">Item:</span>
                    <span className="font-semibold text-zinc-100">Vintage Painting</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-zinc-400">Current Price:</span>
                    <span className="font-semibold text-cyan-300">$50.00</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-zinc-400">Highest Bidder:</span>
                    <span className="font-semibold text-zinc-100">No one</span>
                  </div>
                </div>
                <div>
                  <h3 className="text-lg font-bold text-cyan-400 mt-4 mb-2">Bid History</h3>
                  <div className="text-zinc-400 text-sm">Waiting for auction to start...</div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* User Interface (Demo UI) */}
        {mode === MODES.USER && (
          <div className="p-8">
            <header className="mb-8 text-center">
              <h1 className="text-3xl font-bold bg-gradient-to-r from-cyan-400 to-pink-400 bg-clip-text text-transparent mb-2">
                🏛️ Auction House
              </h1>
              <div className="flex justify-center gap-4 items-center">
                <span className="bg-cyan-400/20 text-cyan-300 px-3 py-1 rounded-full font-mono text-sm">User-0</span>
                <span className="text-green-400">Connected</span>
              </div>
            </header>
            <div className="grid gap-6 md:grid-cols-2 mb-8">
              <div className="bg-[#23234a] rounded-xl p-6 border border-zinc-700 flex flex-col gap-2">
                <div className="text-zinc-400">Item</div>
                <div className="font-semibold text-zinc-100">Vintage Painting</div>
              </div>
              <div className="bg-[#23234a] rounded-xl p-6 border border-zinc-700 flex flex-col gap-2">
                <div className="text-zinc-400">Current Price</div>
                <div className="font-semibold text-cyan-300">$50.00</div>
              </div>
              <div className="bg-[#23234a] rounded-xl p-6 border border-zinc-700 flex flex-col gap-2">
                <div className="text-zinc-400">Highest Bidder</div>
                <div className="font-semibold text-zinc-100">No one</div>
              </div>
              <div className="bg-[#23234a] rounded-xl p-6 border border-zinc-700 flex flex-col gap-2">
                <div className="text-zinc-400">Time Remaining</div>
                <div className="font-mono text-lg text-cyan-300">Waiting to start...</div>
              </div>
            </div>
            <div className="mb-6">
              <h3 className="text-lg font-bold text-cyan-400 mb-2">Bid History</h3>
              <div className="text-zinc-400 text-sm">No bids yet.</div>
            </div>
            <form className="flex gap-2 max-w-md mx-auto">
              <input
                className="flex-1 rounded-lg px-4 py-2 bg-[#23234a] border border-zinc-700 text-zinc-100 focus:outline-none focus:border-pink-400"
                type="number"
                placeholder="Enter your bid amount"
                min={0}
                step={0.01}
                required
                disabled
              />
              <button
                type="submit"
                className="rounded-lg px-6 py-2 font-semibold bg-gradient-to-r from-pink-400 to-cyan-400 text-white hover:from-pink-300 hover:to-cyan-300 transition"
                disabled
              >
                Place Bid
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}

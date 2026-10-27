
import { useCallback, useEffect, useState } from "react";
import {
  Activity,
  CheckCircle2,
  AlertTriangle,
  Clock3,
  RefreshCw,
  Search,
  ShieldCheck,
  Zap,
} from "lucide-react";

import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart,
  Pie,
  Cell,
} from "recharts";

import "./App.css";

const API = "http://localhost:8080";

const CHART_COLORS = ["#2dd4bf", "#f59e0b", "#ef4444", "#8b5cf6"];

export default function App() {
  const [apiKey, setApiKey] = useState("");
  const [keyInput, setKeyInput] = useState("");
  const [logs, setLogs] = useState([]);
  const [stats, setStats] = useState({
    total: 0,
    successful: 0,
    failed: 0,
    average: 0,
  });

  const [search, setSearch] = useState("");
  const [method, setMethod] = useState("ALL");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [updated, setUpdated] = useState("");

  const loadData = useCallback(async () => {
    if (!apiKey) return;

    setLoading(true);
    setError("");

    const endpoints = [
      { key: "total", path: "/traffic/stats/total" },
      { key: "successful", path: "/traffic/stats/successful" },
      { key: "failed", path: "/traffic/stats/failed" },
      { key: "average", path: "/traffic/stats/average-response-time" },
      { key: "logs", path: "/traffic/logs" },
    ];

    try {
      const results = await Promise.all(
        endpoints.map(async ({ key, path }) => {
          const response = await fetch(`${API}${path}`, {
            method: "GET",
            headers: {
              Accept: "application/json",
              "X-API-KEY": apiKey,
            },
          });

          if (!response.ok) {
            throw new Error(
              `${path}: HTTP ${response.status} ${response.statusText}`
            );
          }

          const data = await response.json();
          return { key, data };
        })
      );

      const resultMap = Object.fromEntries(
        results.map(({ key, data }) => [key, data])
      );

      if (!Array.isArray(resultMap.logs)) {
        throw new Error(
          "The /traffic/logs endpoint must return a JSON array."
        );
      }

      setStats({
        total: Number(resultMap.total) || 0,
        successful: Number(resultMap.successful) || 0,
        failed: Number(resultMap.failed) || 0,
        average: Number(resultMap.average) || 0,
      });

      setLogs(resultMap.logs);
      setUpdated(new Date().toLocaleTimeString());
    } catch (err) {
      console.error("Dashboard API error:", err);

      setError(
        err instanceof TypeError
          ? "Cannot connect to backend. Check Spring Boot, port 8080, and CORS."
          : err.message
      );
    } finally {
      setLoading(false);
    }
  }, [apiKey]);

  // Load data immediately and refresh every 30 seconds.
  useEffect(() => {
    if (!apiKey) return undefined;

    loadData();

    const timer = setInterval(() => {
      loadData();
    }, 30000);

    return () => clearInterval(timer);
  }, [apiKey, loadData]);

  const connect = (event) => {
    event.preventDefault();

    const value = keyInput.trim();

    if (!value) {
      setError("Please enter your API key.");
      return;
    }

    setError("");
    setApiKey(value);
  };

  const disconnect = () => {
    setApiKey("");
    setKeyInput("");
    setLogs([]);
    setStats({
      total: 0,
      successful: 0,
      failed: 0,
      average: 0,
    });
    setError("");
    setUpdated("");
  };

  const filteredLogs = logs.filter((log) => {
    const text = [
      log.apiPath,
      log.httpMethod,
      log.statusCode,
    ]
      .join(" ")
      .toLowerCase();

    return (
      text.includes(search.toLowerCase()) &&
      (method === "ALL" || log.httpMethod === method)
    );
  });

  const dateCounts = {};

  logs.forEach((log) => {
    if (!log.requestTime) return;

    const date = new Date(log.requestTime).toLocaleDateString();
    dateCounts[date] = (dateCounts[date] || 0) + 1;
  });

  const chartData = Object.entries(dateCounts)
    .map(([date, requests]) => ({ date, requests }))
    .slice(-7);

  const statusCounts = {};

  logs.forEach((log) => {
    const status = String(log.statusCode ?? "Unknown");
    statusCounts[status] = (statusCounts[status] || 0) + 1;
  });

  const pieData = Object.entries(statusCounts).map(([name, value]) => ({
    name,
    value,
  }));

  const cards = [
    {
      label: "Total Requests",
      value: stats.total.toLocaleString(),
      note: "All recorded requests",
      icon: Activity,
      color: "blue",
    },
    {
      label: "Successful",
      value: stats.successful.toLocaleString(),
      note: "HTTP 200–299",
      icon: CheckCircle2,
      color: "green",
    },
    {
      label: "Failed Requests",
      value: stats.failed.toLocaleString(),
      note: "HTTP 400 and above",
      icon: AlertTriangle,
      color: "red",
    },
    {
      label: "Average Response",
      value: `${stats.average.toFixed(1)} ms`,
      note: "Average response time",
      icon: Clock3,
      color: "purple",
    },
  ];

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="logo">
          <div className="logo-icon">
            <Zap size={23} />
          </div>
          <div>
            <strong>TrafficFlow</strong>
            <small>API MANAGEMENT</small>
          </div>
        </div>

        <p className="nav-heading">WORKSPACE</p>

        <div className="nav-link active">
          <Activity size={18} />
          Overview
        </div>

        <div className="nav-link">
          <ShieldCheck size={18} />
          API Monitoring
        </div>

        <div className="nav-link">
          <Clock3 size={18} />
          Performance
        </div>

        <div className="backend-status">
          <span className="online-dot" />
          <div>
            <strong>Spring Boot</strong>
            <small>localhost:8080</small>
          </div>
        </div>
      </aside>

      <main className="main">
        <header className="topbar">
          <span className="breadcrumb">Workspace / Overview</span>

          <button
            className="refresh"
            onClick={loadData}
            disabled={!apiKey || loading}
          >
            <RefreshCw
              size={16}
              className={loading ? "spin" : ""}
            />
            {loading ? "Loading..." : "Refresh"}
          </button>
        </header>

        <section className="heading">
          <div>
            <p className="eyebrow">API OBSERVABILITY</p>
            <h1>Traffic Overview</h1>
            <p className="muted">
              Monitor requests, performance and API health.
            </p>
          </div>

          <div className="live-pill">
            <span className="online-dot" />
            {apiKey ? "Auto-refresh every 30 sec" : "Not connected"}
          </div>
        </section>

        <section className="connect-panel">
          <div className="connect-description">
            <div className="key-icon">
              <ShieldCheck size={21} />
            </div>

            <div>
              <strong>Connect your backend</strong>
              <p>Enter the API key generated by your backend.</p>
            </div>
          </div>

          <form className="key-form" onSubmit={connect}>
            <input
              type="password"
              placeholder="Enter your API key"
              value={keyInput}
              onChange={(event) => setKeyInput(event.target.value)}
              autoComplete="off"
              aria-label="API key"
            />

            <button type="submit">Connect</button>
          </form>

          {apiKey && (
            <button
              type="button"
              className="disconnect"
              onClick={disconnect}
            >
              Disconnect
            </button>
          )}
        </section>

        {error && (
          <div className="error-message" role="alert">
            {error}
          </div>
        )}

        <section className="stats-grid">
          {cards.map((card) => {
            const Icon = card.icon;

            return (
              <article className="stat-card" key={card.label}>
                <div className="stat-card-top">
                  <span>{card.label}</span>
                  <div className={`stat-icon ${card.color}`}>
                    <Icon size={20} />
                  </div>
                </div>

                <h2>{apiKey ? card.value : "—"}</h2>
                <p>{card.note}</p>
              </article>
            );
          })}
        </section>

        <section className="charts-grid">
          <article className="panel">
            <div className="panel-heading">
              <div>
                <h3>Request Activity</h3>
                <p>Requests grouped by date</p>
              </div>
              <span className="tag">TRAFFIC</span>
            </div>

            <div className="chart">
              {chartData.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={chartData}>
                    <defs>
                      <linearGradient
                        id="areaFill"
                        x1="0"
                        y1="0"
                        x2="0"
                        y2="1"
                      >
                        <stop
                          offset="0%"
                          stopColor="#2dd4bf"
                          stopOpacity={0.3}
                        />
                        <stop
                          offset="100%"
                          stopColor="#2dd4bf"
                          stopOpacity={0.02}
                        />
                      </linearGradient>
                    </defs>

                    <CartesianGrid
                      stroke="#263145"
                      strokeDasharray="3 3"
                      vertical={false}
                    />
                    <XAxis
                      dataKey="date"
                      tick={{ fill: "#94a3b8", fontSize: 11 }}
                    />
                    <YAxis
                      allowDecimals={false}
                      tick={{ fill: "#94a3b8", fontSize: 11 }}
                    />
                    <Tooltip />
                    <Area
                      type="monotone"
                      dataKey="requests"
                      stroke="#2dd4bf"
                      strokeWidth={2.5}
                      fill="url(#areaFill)"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              ) : (
                <div className="empty">
                  {apiKey
                    ? "No traffic data yet."
                    : "Connect your API to view traffic."}
                </div>
              )}
            </div>
          </article>

          <article className="panel">
            <div className="panel-heading">
              <div>
                <h3>HTTP Status Codes</h3>
                <p>Distribution from saved logs</p>
              </div>
            </div>

            <div className="pie-chart">
              {pieData.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={pieData}
                      dataKey="value"
                      nameKey="name"
                      innerRadius={48}
                      outerRadius={75}
                      paddingAngle={3}
                    >
                      {pieData.map((item, index) => (
                        <Cell
                          key={item.name}
                          fill={
                            CHART_COLORS[index % CHART_COLORS.length]
                          }
                        />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <div className="empty">No status-code data yet.</div>
              )}
            </div>

            <div className="legend">
              {pieData.slice(0, 5).map((item, index) => (
                <div key={item.name}>
                  <span
                    className="legend-dot"
                    style={{
                      background:
                        CHART_COLORS[index % CHART_COLORS.length],
                    }}
                  />
                  <span>HTTP {item.name}</span>
                  <strong>{item.value}</strong>
                </div>
              ))}
            </div>
          </article>
        </section>

        <section className="panel logs-panel">
          <div className="panel-heading logs-heading">
            <div>
              <h3>API Request Logs</h3>
              <p>{filteredLogs.length} matching records</p>
            </div>

            <div className="filters">
              <div className="search-box">
                <Search size={16} />
                <input
                  placeholder="Search endpoint..."
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
                />
              </div>

              <select
                value={method}
                onChange={(event) => setMethod(event.target.value)}
              >
                <option value="ALL">All methods</option>
                <option value="GET">GET</option>
                <option value="POST">POST</option>
                <option value="PUT">PUT</option>
                <option value="DELETE">DELETE</option>
              </select>
            </div>
          </div>

          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ENDPOINT</th>
                  <th>METHOD</th>
                  <th>STATUS</th>
                  <th>RESPONSE</th>
                  <th>REQUEST TIME</th>
                </tr>
              </thead>

              <tbody>
                {filteredLogs
                  .slice()
                  .reverse()
                  .slice(0, 50)
                  .map((log, index) => (
                    <tr key={log.id ?? index}>
                      <td className="endpoint">
                        {log.apiPath ?? "—"}
                      </td>

                      <td>
                        <span
                          className={`method ${String(
                            log.httpMethod ?? ""
                          ).toLowerCase()}`}
                        >
                          {log.httpMethod ?? "—"}
                        </span>
                      </td>

                      <td>
                        <span
                          className={`status ${
                            Number(log.statusCode) >= 400
                              ? "failed"
                              : Number(log.statusCode) >= 300
                                ? "warning"
                                : "success"
                          }`}
                        >
                          {log.statusCode ?? "—"}
                        </span>
                      </td>

                      <td>{log.responseTime ?? "—"} ms</td>

                      <td>
                        {log.requestTime
                          ? new Date(log.requestTime).toLocaleString()
                          : "—"}
                      </td>
                    </tr>
                  ))}

                {filteredLogs.length === 0 && (
                  <tr>
                    <td colSpan="5" className="no-logs">
                      {apiKey
                        ? "No matching request logs found."
                        : "Connect your API key to load logs."}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="table-footer">
            <span>Showing up to 50 records</span>
            <span>
              {updated
                ? `Last updated ${updated}`
                : "Waiting for connection"}
            </span>
          </div>
        </section>

        <footer className="footer">
          <span>TrafficFlow · Smart API Traffic Manager</span>
          <span>
            <ShieldCheck size={15} />
            API key connection
          </span>
        </footer>
      </main>
    </div>
  );
}

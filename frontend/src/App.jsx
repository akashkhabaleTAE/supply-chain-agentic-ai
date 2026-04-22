import { useEffect, useMemo, useState } from "react";
import {
  Activity,
  AlertTriangle,
  CheckCircle2,
  Factory,
  Gauge,
  LineChart,
  Loader2,
  Lock,
  LogOut,
  Play,
  RefreshCw,
  Route,
  ShieldAlert,
  Truck
} from "lucide-react";
import {
  Bar,
  Doughnut,
  Line
} from "react-chartjs-2";
import {
  ArcElement,
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Filler,
  Legend,
  LinearScale,
  LineElement,
  PointElement,
  Tooltip
} from "chart.js";

ChartJS.register(
  ArcElement,
  BarElement,
  CategoryScale,
  Filler,
  Legend,
  LinearScale,
  LineElement,
  PointElement,
  Tooltip
);

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const TOKEN_KEY = "supplyChainAiToken";
const DEMO_EVENT =
  "Hurricane Taiwan may disrupt semiconductor port operations and chip shipments";

const severityColors = {
  CRITICAL: "#b42318",
  HIGH: "#d97008",
  MEDIUM: "#b7791f",
  LOW: "#0e766e"
};

const riskPalette = ["#b42318", "#d97008", "#b7791f", "#0e766e"];

function authHeaders(token) {
  return {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json"
  };
}

async function requestJson(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, options);
  if (!response.ok) {
    const body = await response.text();
    throw new Error(body || `HTTP ${response.status}`);
  }
  return response.json();
}

function formatDate(value) {
  if (!value) {
    return "n/a";
  }
  return new Intl.DateTimeFormat("en-IN", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}

function formatMoney(value) {
  if (value === null || value === undefined) {
    return "n/a";
  }
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(Number(value));
}

function riskClass(score) {
  if (score >= 85) return "critical";
  if (score >= 70) return "high";
  if (score >= 45) return "medium";
  return "low";
}

function severityClass(severity) {
  return String(severity || "LOW").toLowerCase();
}

function App() {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) || "");
  const [username, setUsername] = useState("akash");
  const [password, setPassword] = useState("supplychain2026");
  const [dashboard, setDashboard] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [eventText, setEventText] = useState(DEMO_EVENT);
  const [loading, setLoading] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (token) {
      loadDashboard(token);
    }
  }, [token]);

  async function login(event) {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const response = await requestJson("/auth/token", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });
      localStorage.setItem(TOKEN_KEY, response.accessToken);
      setToken(response.accessToken);
    } catch (loginError) {
      setError("Gateway login failed. Check Docker health and credentials.");
    } finally {
      setLoading(false);
    }
  }

  async function loadDashboard(activeToken = token) {
    setError("");
    setLoading(true);
    try {
      const data = await requestJson("/api/v1/dashboard/data", {
        headers: authHeaders(activeToken)
      });
      setDashboard(data);
    } catch (dashboardError) {
      setError("Dashboard data is unavailable. Refresh the token or check gateway logs.");
    } finally {
      setLoading(false);
    }
  }

  async function analyzeEvent(event) {
    event.preventDefault();
    setError("");
    setAnalyzing(true);
    try {
      const response = await requestJson("/api/v1/analyze", {
        method: "POST",
        headers: authHeaders(token),
        body: JSON.stringify({
          event: eventText,
          supplyChainId: 1
        })
      });
      setAnalysis(response);
      await loadDashboard(token);
    } catch (analysisError) {
      setError("Agent workflow failed. Confirm all Docker services are healthy.");
    } finally {
      setAnalyzing(false);
    }
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY);
    setToken("");
    setDashboard(null);
    setAnalysis(null);
  }

  const severityChart = useMemo(() => {
    const severity = dashboard?.riskEventsBySeverity || {};
    const labels = ["CRITICAL", "HIGH", "MEDIUM", "LOW"];
    return {
      labels,
      datasets: [
        {
          data: labels.map((label) => Number(severity[label] || 0)),
          backgroundColor: labels.map((label) => severityColors[label]),
          borderWidth: 0
        }
      ]
    };
  }, [dashboard]);

  const heatmapChart = useMemo(() => {
    const labels = dashboard?.heatmapLabels || [];
    const values = dashboard?.heatmapScores || [];
    return {
      labels,
      datasets: [
        {
          label: "Exposure",
          data: values,
          backgroundColor: values.map((score) => {
            if (score >= 85) return "#b42318";
            if (score >= 70) return "#d97008";
            if (score >= 45) return "#b7791f";
            return "#0e766e";
          }),
          borderRadius: 6,
          maxBarThickness: 44
        }
      ]
    };
  }, [dashboard]);

  const trendChart = useMemo(() => {
    const events = [...(dashboard?.latestEvents || [])].reverse();
    return {
      labels: events.map((item) => formatDate(item.detectedAt)),
      datasets: [
        {
          label: "Exposure trend",
          data: events.map((item) => item.exposureScore || 0),
          borderColor: "#0e766e",
          backgroundColor: "rgba(14, 118, 110, 0.12)",
          fill: true,
          tension: 0.35,
          pointRadius: 3,
          pointBackgroundColor: "#0e766e"
        }
      ]
    };
  }, [dashboard]);

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "bottom",
        labels: {
          boxWidth: 10,
          boxHeight: 10,
          color: "#49534f",
          font: { size: 11 }
        }
      },
      tooltip: {
        backgroundColor: "#17211f",
        titleColor: "#ffffff",
        bodyColor: "#eef3ef",
        padding: 10
      }
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: { color: "#66716c", font: { size: 11 } }
      },
      y: {
        beginAtZero: true,
        suggestedMax: 100,
        grid: { color: "#e4e8e4" },
        ticks: { color: "#66716c", font: { size: 11 } }
      }
    }
  };

  if (!token) {
    return (
      <main className="login-shell">
        <section className="login-panel">
          <div className="brand-mark">
            <ShieldAlert size={28} />
          </div>
          <div>
            <p className="eyebrow">Supply Chain AI</p>
            <h1>Operations Command</h1>
          </div>
          <form className="login-form" onSubmit={login}>
            <label>
              Username
              <input
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                autoComplete="username"
              />
            </label>
            <label>
              Password
              <input
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                type="password"
                autoComplete="current-password"
              />
            </label>
            {error && <p className="error-text">{error}</p>}
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? <Loader2 className="spin" size={18} /> : <Lock size={18} />}
              Sign in
            </button>
          </form>
        </section>
      </main>
    );
  }

  return (
    <main className="app-shell">
      <aside className="side-nav" aria-label="Supply chain workspace">
        <div className="brand-lockup">
          <div className="brand-mark">
            <ShieldAlert size={24} />
          </div>
          <div>
            <strong>Supply Chain AI</strong>
            <span>Autonomous risk ops</span>
          </div>
        </div>
        <nav>
          <a className="nav-item active" href="#overview">
            <Gauge size={18} />
            Overview
          </a>
          <a className="nav-item" href="#workflow">
            <Activity size={18} />
            Agent Workflow
          </a>
          <a className="nav-item" href="#suppliers">
            <Factory size={18} />
            Suppliers
          </a>
          <a className="nav-item" href="#events">
            <AlertTriangle size={18} />
            Events
          </a>
        </nav>
      </aside>

      <section className="workspace">
        <header className="top-bar">
          <div>
            <p className="eyebrow">Supply chain 1</p>
            <h1>Risk Control Tower</h1>
          </div>
          <div className="header-actions">
            <button
              className="icon-button"
              type="button"
              title="Refresh dashboard"
              onClick={() => loadDashboard()}
              disabled={loading}
            >
              {loading ? <Loader2 className="spin" size={18} /> : <RefreshCw size={18} />}
            </button>
            <button className="icon-button" type="button" title="Sign out" onClick={logout}>
              <LogOut size={18} />
            </button>
          </div>
        </header>

        {error && <div className="alert-banner">{error}</div>}

        <section id="overview" className="metric-grid">
          <MetricCard
            icon={<Gauge size={20} />}
            label="Max Exposure"
            value={dashboard ? dashboard.maxExposureScore.toFixed(1) : "--"}
            tone={riskClass(dashboard?.maxExposureScore || 0)}
          />
          <MetricCard
            icon={<AlertTriangle size={20} />}
            label="Open Events"
            value={dashboard?.openRiskEvents ?? "--"}
            tone="medium"
          />
          <MetricCard
            icon={<Factory size={20} />}
            label="High Risk Suppliers"
            value={dashboard?.criticalOrHighSuppliers ?? "--"}
            tone="high"
          />
          <MetricCard
            icon={<CheckCircle2 size={20} />}
            label="Generated"
            value={dashboard ? formatDate(dashboard.generatedAt) : "--"}
            tone="low"
          />
        </section>

        <section className="main-grid">
          <div className="panel chart-panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Exposure</p>
                <h2>Supplier Risk Heatmap</h2>
              </div>
              <LineChart size={20} />
            </div>
            <div className="chart-box tall">
              <Bar data={heatmapChart} options={chartOptions} />
            </div>
          </div>

          <div className="panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Severity</p>
                <h2>Event Mix</h2>
              </div>
              <AlertTriangle size={20} />
            </div>
            <div className="chart-box">
              <Doughnut
                data={severityChart}
                options={{
                  ...chartOptions,
                  cutout: "68%",
                  scales: {}
                }}
              />
            </div>
          </div>

          <div className="panel chart-panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Timeline</p>
                <h2>Exposure Trend</h2>
              </div>
              <Activity size={20} />
            </div>
            <div className="chart-box">
              <Line data={trendChart} options={chartOptions} />
            </div>
          </div>
        </section>

        <section id="workflow" className="workflow-grid">
          <form className="panel analyze-panel" onSubmit={analyzeEvent}>
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Agentic workflow</p>
                <h2>Disruption Simulation</h2>
              </div>
              <Route size={20} />
            </div>
            <textarea
              value={eventText}
              onChange={(event) => setEventText(event.target.value)}
              rows={5}
            />
            <button className="primary-button" type="submit" disabled={analyzing}>
              {analyzing ? <Loader2 className="spin" size={18} /> : <Play size={18} />}
              Run agents
            </button>
          </form>

          <div className="panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Last run</p>
                <h2>Agent Trace</h2>
              </div>
              <Activity size={20} />
            </div>
            <div className="trace-list">
              {(analysis?.agentTrace || []).map((step) => (
                <div className="trace-row" key={step.agentName}>
                  <span className="trace-dot" />
                  <div>
                    <strong>{step.agentName}</strong>
                    <p>{step.summary}</p>
                  </div>
                  <small>{step.durationMs} ms</small>
                </div>
              ))}
              {!analysis && <EmptyState label="No workflow run in this browser session" />}
            </div>
          </div>
        </section>

        {analysis && (
          <section className="panel report-panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Executive report</p>
                <h2>{analysis.report.disruptionTitle}</h2>
              </div>
              <span className={`risk-pill ${riskClass(analysis.report.exposureScore)}`}>
                {analysis.report.riskLevel} {analysis.report.exposureScore.toFixed(1)}
              </span>
            </div>
            <p className="summary-copy">{analysis.report.executiveSummary}</p>
            <div className="option-grid">
              {analysis.report.options.map((option) => (
                <article className="option-card" key={`${option.optionType}-${option.title}`}>
                  <div>
                    <span>{option.optionType}</span>
                    <h3>{option.title}</h3>
                  </div>
                  <p>{option.rationale}</p>
                  <footer>
                    <strong>{Math.round(option.confidenceScore * 100)}%</strong>
                    <span>{option.estimatedRecoveryDays} days</span>
                    <span>{formatMoney(option.estimatedCostImpact)}</span>
                  </footer>
                </article>
              ))}
            </div>
          </section>
        )}

        <section className="table-grid">
          <div id="events" className="panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Detected disruptions</p>
                <h2>Latest Events</h2>
              </div>
              <Truck size={20} />
            </div>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Event</th>
                    <th>Location</th>
                    <th>Severity</th>
                    <th>Exposure</th>
                  </tr>
                </thead>
                <tbody>
                  {(dashboard?.latestEvents || []).map((event) => (
                    <tr key={event.id}>
                      <td>
                        <strong>{event.title}</strong>
                        <span>{event.type} / {event.status}</span>
                      </td>
                      <td>{event.location}</td>
                      <td>
                        <span className={`severity-pill ${severityClass(event.severity)}`}>
                          {event.severity}
                        </span>
                      </td>
                      <td>{Number(event.exposureScore || 0).toFixed(1)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div id="suppliers" className="panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">Network exposure</p>
                <h2>Critical Suppliers</h2>
              </div>
              <Factory size={20} />
            </div>
            <div className="supplier-list">
              {(dashboard?.highRiskSuppliers || []).map((supplier, index) => (
                <article className="supplier-card" key={supplier.supplierCode}>
                  <div className="supplier-score" style={{ borderColor: riskPalette[index % riskPalette.length] }}>
                    T{supplier.tier}
                  </div>
                  <div>
                    <strong>{supplier.name}</strong>
                    <span>{supplier.region}, {supplier.country}</span>
                    <small>{supplier.materialType} / {supplier.leadTimeDays} days</small>
                  </div>
                  <span className={`risk-pill ${severityClass(supplier.baselineRisk)}`}>
                    {supplier.baselineRisk}
                  </span>
                </article>
              ))}
            </div>
          </div>
        </section>
      </section>
    </main>
  );
}

function MetricCard({ icon, label, value, tone }) {
  return (
    <article className={`metric-card ${tone}`}>
      <div className="metric-icon">{icon}</div>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function EmptyState({ label }) {
  return (
    <div className="empty-state">
      <Activity size={22} />
      <span>{label}</span>
    </div>
  );
}

export default App;

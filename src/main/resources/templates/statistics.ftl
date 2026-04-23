<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Statistics Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .dashboard-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
        }
        .metric-card {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            transition: transform 0.3s;
        }
        .metric-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
        }
        .success-badge {
            color: #28a745;
            font-weight: bold;
        }
        .failure-badge {
            color: #dc3545;
            font-weight: bold;
        }
        .nav-pills .nav-link.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .table-hover tbody tr:hover {
            background-color: #f8f9fa;
        }
        .chart-container {
            position: relative;
            height: 300px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1 class="text-white mb-4">
        <i class="bi bi-graph-up"></i> Statistics Dashboard
    </h1>

    <!-- Summary Cards -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="dashboard-card text-center">
                <h3 class="text-primary" id="total-metrics">0</h3>
                <p class="text-muted mb-0">Total Methods (Metrics)</p>
            </div>
        </div>
        <div class="col-md-3">
            <div class="dashboard-card text-center">
                <h3 class="text-success" id="total-benchmarks">0</h3>
                <p class="text-muted mb-0">Total Methods (Benchmark)</p>
            </div>
        </div>
        <div class="col-md-3">
            <div class="dashboard-card text-center">
                <h3 class="text-info" id="total-success">0</h3>
                <p class="text-muted mb-0">Total Success Calls</p>
            </div>
        </div>
        <div class="col-md-3">
            <div class="dashboard-card text-center">
                <h3 class="text-warning" id="total-failures">0</h3>
                <p class="text-muted mb-0">Total Failed Calls</p>
            </div>
        </div>
    </div>

    <!-- Tabs -->
    <div class="dashboard-card">
        <ul class="nav nav-pills mb-4" id="statistics-tabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="metrics-tab" data-bs-toggle="pill" data-bs-target="#metrics" type="button" role="tab">
                    <i class="bi bi-bar-chart"></i> Metrics
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="benchmark-tab" data-bs-toggle="pill" data-bs-target="#benchmark" type="button" role="tab">
                    <i class="bi bi-clock"></i> Benchmark
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="percentile-tab" data-bs-toggle="pill" data-bs-target="#percentile" type="button" role="tab">
                    <i class="bi bi-percent"></i> Percentile Calculator
                </button>
            </li>
        </ul>

        <div class="tab-content">
            <!-- Metrics Tab -->
            <div class="tab-pane fade show active" id="metrics" role="tabpanel">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4>Method Metrics</h4>
                    <button class="btn btn-outline-primary btn-sm" onclick="refreshData()">
                        <i class="bi bi-arrow-clockwise"></i> Refresh
                    </button>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                        <tr>
                            <th>Method</th>
                            <th class="text-center">Success</th>
                            <th class="text-center">Failed</th>
                            <th class="text-center">Total</th>
                            <th class="text-center">Success Rate</th>
                        </tr>
                        </thead>
                        <tbody id="metrics-table-body">
                        <tr>
                            <td colspan="5" class="text-center text-muted">Loading...</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="chart-container">
                    <canvas id="metrics-chart"></canvas>
                </div>
            </div>

            <!-- Benchmark Tab -->
            <div class="tab-pane fade" id="benchmark" role="tabpanel">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4>Benchmark Statistics</h4>
                    <div>
                        <button class="btn btn-outline-primary btn-sm me-2" onclick="refreshData()">
                            <i class="bi bi-arrow-clockwise"></i> Refresh
                        </button>
                        <button class="btn btn-outline-danger btn-sm" onclick="clearBenchmarkStats()">
                            <i class="bi bi-trash"></i> Clear All
                        </button>
                    </div>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                        <tr>
                            <th>Method</th>
                            <th class="text-center">Count</th>
                            <th class="text-center">Min (ms)</th>
                            <th class="text-center">Max (ms)</th>
                            <th class="text-center">Avg (ms)</th>
                            <th class="text-center">P50 (ms)</th>
                            <th class="text-center">P95 (ms)</th>
                            <th class="text-center">P99 (ms)</th>
                        </tr>
                        </thead>
                        <tbody id="benchmark-table-body">
                        <tr>
                            <td colspan="8" class="text-center text-muted">Loading...</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="chart-container">
                    <canvas id="benchmark-chart"></canvas>
                </div>
            </div>

            <!-- Percentile Calculator Tab -->
            <div class="tab-pane fade" id="percentile" role="tabpanel">
                <h4 class="mb-4">Percentile Calculator</h4>
                <div class="row">
                    <div class="col-md-6">
                        <div class="metric-card">
                            <form onsubmit="calculatePercentile(event)">
                                <div class="mb-3">
                                    <label for="method-select" class="form-label">Select Method</label>
                                    <select class="form-select" id="method-select" required>
                                        <option value="">Choose a method...</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="percentile-input" class="form-label">Percentile (0-100)</label>
                                    <input type="number" class="form-control" id="percentile-input"
                                           min="0" max="100" value="95" required>
                                </div>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-calculator"></i> Calculate
                                </button>
                            </form>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="metric-card" id="percentile-result" style="display: none;">
                            <h5>Result</h5>
                            <hr>
                            <p><strong>Method:</strong> <span id="result-method"></span></p>
                            <p><strong>Percentile:</strong> <span id="result-percentile"></span>%</p>
                            <p><strong>Value:</strong> <span id="result-value"></span> ms</p>
                            <p><strong>Total Samples:</strong> <span id="result-samples"></span></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    let metricsChart = null;
    let benchmarkChart = null;
    let currentData = { metrics: {}, benchmarks: {} };

    document.addEventListener('DOMContentLoaded', function() {
        refreshData();
    });

    async function refreshData() {
        try {
            await Promise.all([
                loadMetrics(),
                loadBenchmarks()
            ]);
            updateCharts();
            updateMethodSelect();
        } catch (error) {
            console.error('Error loading data:', error);
        }
    }

    async function loadMetrics() {
        const response = await fetch('/api/statistics/metrics');
        const data = await response.json();
        currentData.metrics = data.metrics || {};

        const tbody = document.getElementById('metrics-table-body');
        if (Object.keys(currentData.metrics).length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No metrics data available</td></tr>';
            return;
        }

        let html = '';
        let totalSuccess = 0;
        let totalFailures = 0;

        for (const [method, stats] of Object.entries(currentData.metrics)) {
            html += '<tr>' +
                '<td><code>' + method + '</code></td>' +
                '<td class="text-center success-badge">' + stats.successCount + '</td>' +
                '<td class="text-center failure-badge">' + stats.failureCount + '</td>' +
                '<td class="text-center">' + stats.totalCount + '</td>' +
                '<td class="text-center">' +
                '<div class="progress" style="height: 20px;">' +
                '<div class="progress-bar bg-success" style="width: ' + stats.successRate + '%">' +
                stats.successRate.toFixed(1) + '%' +
                '</div>' +
                '</div>' +
                '</td>' +
                '</tr>';
            totalSuccess += stats.successCount;
            totalFailures += stats.failureCount;
        }
        tbody.innerHTML = html;

        document.getElementById('total-success').textContent = totalSuccess;
        document.getElementById('total-failures').textContent = totalFailures;
        document.getElementById('total-metrics').textContent = Object.keys(currentData.metrics).length;
    }

    async function loadBenchmarks() {
        const response = await fetch('/api/statistics/benchmark');
        const data = await response.json();
        currentData.benchmarks = data.stats || {};

        const tbody = document.getElementById('benchmark-table-body');
        if (Object.keys(currentData.benchmarks).length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No benchmark data available</td></tr>';
            return;
        }

        let html = '';
        for (const [method, stats] of Object.entries(currentData.benchmarks)) {
            html += '<tr>' +
                '<td><code>' + method + '</code></td>' +
                '<td class="text-center">' + stats.count + '</td>' +
                '<td class="text-center">' + stats.minMillis.toFixed(3) + '</td>' +
                '<td class="text-center">' + stats.maxMillis.toFixed(3) + '</td>' +
                '<td class="text-center">' + stats.avgMillis.toFixed(3) + '</td>' +
                '<td class="text-center">' + stats.p50Millis.toFixed(3) + '</td>' +
                '<td class="text-center">' + stats.p95Millis.toFixed(3) + '</td>' +
                '<td class="text-center">' + stats.p99Millis.toFixed(3) + '</td>' +
                '</tr>';
        }
        tbody.innerHTML = html;
        document.getElementById('total-benchmarks').textContent = Object.keys(currentData.benchmarks).length;
    }

    function updateCharts() {
        updateMetricsChart();
        updateBenchmarkChart();
    }

    function updateMetricsChart() {
        const ctx = document.getElementById('metrics-chart').getContext('2d');
        const methods = Object.keys(currentData.metrics);
        const successData = methods.map(m => currentData.metrics[m].successCount);
        const failureData = methods.map(m => currentData.metrics[m].failureCount);

        if (metricsChart) {
            metricsChart.destroy();
        }

        if (methods.length === 0) return;

        metricsChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: methods,
                datasets: [
                    {
                        label: 'Success',
                        data: successData,
                        backgroundColor: '#28a745'
                    },
                    {
                        label: 'Failed',
                        data: failureData,
                        backgroundColor: '#dc3545'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1 }
                    }
                }
            }
        });
    }

    function updateBenchmarkChart() {
        const ctx = document.getElementById('benchmark-chart').getContext('2d');
        const methods = Object.keys(currentData.benchmarks);
        const avgData = methods.map(m => currentData.benchmarks[m].avgMillis);
        const p95Data = methods.map(m => currentData.benchmarks[m].p95Millis);

        if (benchmarkChart) {
            benchmarkChart.destroy();
        }

        if (methods.length === 0) return;

        benchmarkChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: methods,
                datasets: [
                    {
                        label: 'Average (ms)',
                        data: avgData,
                        backgroundColor: '#007bff'
                    },
                    {
                        label: 'P95 (ms)',
                        data: p95Data,
                        backgroundColor: '#ffc107'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    function updateMethodSelect() {
        const select = document.getElementById('method-select');
        const methods = Object.keys(currentData.benchmarks);

        let html = '<option value="">Choose a method...</option>';
        methods.forEach(method => {
            html += '<option value="' + method + '">' + method + '</option>';
        });
        select.innerHTML = html;
    }

    async function calculatePercentile(event) {
        event.preventDefault();

        const method = document.getElementById('method-select').value;
        const percentile = document.getElementById('percentile-input').value;

        if (!method || !percentile) return;

        try {
            const response = await fetch('/api/statistics/benchmark/percentile?methodName=' + encodeURIComponent(method) + '&percentile=' + percentile);
            const data = await response.json();

            document.getElementById('result-method').textContent = data.methodName;
            document.getElementById('result-percentile').textContent = data.percentile;
            document.getElementById('result-value').textContent = data.valueMillis.toFixed(3);
            document.getElementById('result-samples').textContent = data.totalSamples;
            document.getElementById('percentile-result').style.display = 'block';
        } catch (error) {
            alert('Error calculating percentile: ' + error.message);
        }
    }

    async function clearBenchmarkStats() {
        if (!confirm('Are you sure you want to clear all benchmark statistics?')) return;

        try {
            const response = await fetch('/api/statistics/benchmark/clear', { method: 'DELETE' });
            if (response.ok) {
                refreshData();
            }
        } catch (error) {
            alert('Error clearing statistics: ' + error.message);
        }
    }

    // Auto-refresh every 30 seconds
    setInterval(refreshData, 30000);
</script>
</body>
</html>
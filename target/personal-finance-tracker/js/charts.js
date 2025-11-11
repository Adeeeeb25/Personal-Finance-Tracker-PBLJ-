function renderPieChart(labels, values) {
  const ctx = document.getElementById('pieChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'pie',
    data: {
      labels: labels,
      datasets: [{
        data: values,
        backgroundColor: [
          '#60a5fa', '#f472b6', '#fbbf24', '#34d399', '#a78bfa', '#f87171', '#22d3ee'
        ],
      }]
    },
    options: {
      plugins: { legend: { labels: { color: '#e5e7eb' } } }
    }
  });
}

function renderBarChart(labels, values) {
  const ctx = document.getElementById('barChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Amount ($)',
        data: values,
        backgroundColor: ['#22c55e', '#ef4444']
      }]
    },
    options: {
      scales: {
        x: { ticks: { color: '#e5e7eb' } },
        y: { ticks: { color: '#e5e7eb' } }
      },
      plugins: { legend: { labels: { color: '#e5e7eb' } } }
    }
  });
}



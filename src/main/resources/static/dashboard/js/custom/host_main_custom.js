// Chart.js scripts
// -- Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#292b2c';
// 월별 판매액 선그래프
var ctx = document.getElementById("myAreaChart");
var myLineChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: monthlySalesKey,
        datasets: [{
            label: "월별예약건수",
            lineTension: 0.3,
            backgroundColor: "rgba(2,117,216,0.2)",
            borderColor: "rgba(2,117,216,1)",
            pointRadius: 5,
            pointBackgroundColor: "rgba(2,117,216,1)",
            pointBorderColor: "rgba(255,255,255,0.8)",
            pointHoverRadius: 5,
            pointHoverBackgroundColor: "rgba(2,117,216,1)",
            pointHitRadius: 20,
            pointBorderWidth: 2,
            data: monthlySalesValue,
        }],
    },
    options: {
        scales: {
            xAxes: [{
                time: {
                    unit: 'month'
                },
                gridLines: {
                    display: false
                },
                ticks: {
                    maxTicksLimit: 12,
                    callback: function (value, index, values) {
                        return value;
                    },
                }
            }],
            yAxes: [{
                ticks: {
                    min: 0,
                    max: Math.max(...monthlySalesValue) + (Math.max(...monthlySalesValue) * 0.1),
                    maxTicksLimit: 5,
                    callback: function (value, index, values) {
                        return value.toLocaleString() + '원';
                    },
                },
                gridLines: {
                    color: "rgba(0, 0, 0, .125)",
                }
            }],
        },
        legend: {
            display: false
        },
        tooltips: {
            callbacks: {
                label: function (tooltipItem, data) {
                    const value = tooltipItem.yLabel;
                    return value.toLocaleString() + '원';
                },
            },
        },
    }
});

// 월별 예약 건수 막대그래프
var ctx = document.getElementById("myBarChart");
var myLineChart = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: monthResKey,
        datasets: [{
            label: "Reservation",
            backgroundColor: "rgba(2,117,216,1)",
            borderColor: "rgba(2,117,216,1)",
            data: monthResValue,
        }]
        ,
    },
    options: {
        scales: {
            xAxes: [{
                time: {
                    unit: 'month'
                },
                gridLines: {
                    display: false
                },
                ticks: {
                    maxTicksLimit: 12,
                    callback: function (value, index, values) {
                        return value + '월';
                    },
                }
            }],
            yAxes: [{
                ticks: {
                    min: 0,
                    max: Math.max(...monthResValue) + (Math.max(...monthResValue) * 0.1),
                    maxTicksLimit: 5,
                    callback: function (value, index, values) {
                        return value.toLocaleString() + '건';
                    },
                },
                gridLines: {
                    display: true
                }
            }],
        },
        legend: {
            display: false
        },
        tooltips: {
            callbacks: {
                label: function (tooltipItem, data) {
                    const value = tooltipItem.yLabel;
                    return value.toLocaleString() + '건';
                },
            },
        },
    }
});
function callDatePicker(data) {
  function DisableDates(date, dates = data) {
    var string = jQuery.datepicker.formatDate('dd-mm-yy', date);
    return [dates.indexOf(string) == -1];
  }

  $("#datepicker").datepicker({
    dateFormat: 'dd-mm-yy',
    beforeShowDay: DisableDates,
    minDate: 0
  });

  $('#datepicker').datepicker();
  $('#datepicker').datepicker('show');
}

function jourValue() {
  return $('#datepicker').val();
}

function statistiques1(labels, data1, data2) {
  var ctx = document.getElementById('myChart1').getContext('2d');
  colors1 = []; colors2 = [];
  colors1Brd = []; colors2Brd = [];
  for (let i = 0; i < labels.length; i++) {
    colors1.push('rgb(241, 29, 49, 0.5)');
    colors2.push('rgb(43, 202, 79, 0.5)');
    colors1Brd.push('rgb(241, 29, 49)');
    colors2Brd.push('rgb(43, 202, 79)');
  }
  var myChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Rendez-vous',
        data: data1,
        backgroundColor: colors1,
        borderColor: colors1Brd,
        borderWidth: 1
      }, {
        label: 'vaccination',
        data: data2,
        backgroundColor: colors2,
        borderColor: colors2Brd,
        borderWidth: 1

      }]
    },
    options: {}
  });
}

function statistiques2(labels, data1) {
  var ctx = document.getElementById('myChart2').getContext('2d');
  var chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label: 'Rendez-vous',
        borderColor: '#ff3548',
        backgroundColor: 'transparent',
        data: data1
      }]
    },
    options: {
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: true
          }
        }]
      }
    }
  });
}

function dates() {
  var table = [0, 0, 0, 0, 0, 0, 0]
  const options = { month: 'numeric', day: 'numeric' };
  for (i = 0; i < 7; i++) {
    table[i] = new Date(Date.now() + i * 24 * 60 * 60 * 1000)
      .toLocaleDateString('fr-FR', options);
  }
  return table;
}
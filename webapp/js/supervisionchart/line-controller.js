var app = angular.module( "lineModule", ['datatables', 'ngResource'] )
		
app.controller("lineController", function( $scope, $rootScope, $interval, httpService ) {
	
	var colors = ["rgb(192, 75, 100)", "rgb(100, 75, 100)", "rgb(192, 200, 100)", "rgb(192, 75, 200)", "rgb(192, 75, 0)", "rgb(50, 200, 50)", "rgb(200, 50, 200)", "rgb(150, 50, 200)"]
	
	$scope.updateLine = function(urlAction) {
		console.log("search for " + urlAction)
		httpService.getData("/chart/evolutionUrl", {url: urlAction, debut: new Date(), fin: new Date(), date: new Date()}).then(function(data){
			
			//{url: "commandes.afficherListArtCmdResultat", nb: 0, repartition: Array(5), totalTime: 1000, dateExtraction:{month: "NOVEMBER", year: 2017}}
			
			var extractionValues = data.map(function(e){return e.dateExtraction}).map(function(e){return e.dayOfMonth + "/" + e.monthValue + "/" + e.year})
			
			var avgValues = data.map(function(e){return e.avgTime})
			
			
			var newData = [{"label": urlAction, "data": avgValues, "fill": false, "borderColor": colors.random(), "lineTension": 0.1}]
			
			var labels = ["12/11/2017", "13/11/2017", "14/11/2017"]
			
			var ctx = document.getElementById("myChart").getContext('2d');
			var config = {
				type: 'line',
				data: {
					labels: extractionValues,
					datasets: newData
				},
				options: {
					responsive: false,
					maintainAspectRatio: false
				}
			}
			var myChart = new Chart(ctx, config)
			
			myChart.update()
		})
	}
	
	$scope.updateAction = function() {
		httpService.postData("/chart/updateAction", {}).then(function(data){
			
		})
	}

	// call when app is loaded
	angular.element(document).ready(function () {
		//$scope.updateLine("commandes.afficherListArtCmdResultat")
		$scope.updateLine("commandes.miseAJour")
	});
})
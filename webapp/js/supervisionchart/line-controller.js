var app = angular.module( "lineModule", ['datatables', 'ngResource'] )
		
app.controller("lineController", function( $scope, $rootScope, $interval, httpService ) {
	
	var colors = ["rgb(192, 75, 100)", "rgb(100, 75, 100)", "rgb(192, 200, 100)", "rgb(192, 75, 200)", "rgb(192, 75, 0)", "rgb(50, 200, 50)", "rgb(200, 50, 200)", "rgb(150, 50, 200)"]
	
	var canvasCtx = document.getElementById("myChart").getContext('2d');
	var config = {
			type: 'line',
			data: {},
			options: {
				responsive: false,
				maintainAspectRatio: false
			}
		}
	var myChart = new Chart(canvasCtx, config)
	
	$scope.actionSort = "-avgTime"
	
	$scope.updateUrl = function() {
		httpService.getData("/chart/listUrl", {server: 'lx01', dateDebut: new $('#dateDebut').val(), dateFin: new $('#dateFin').val(), forceRefresh: new Date()}).then(function(data){
			$scope.urls = data
		})
		httpService.getData("/chart/listAction", {server: 'lx01', dateDebut: new $('#dateDebut').val(), dateFin: new $('#dateFin').val(), forceRefresh: new Date()}).then(function(data){
			$scope.actions = data
		})
	}

	$scope.updateLine = function(urlAction) {
		console.log("search for " + urlAction)
		httpService.getData("/chart/evolutionUrl", {server: 'lx01', url: urlAction, dateDebut: new $('#dateDebut').val(), dateFin: new $('#dateFin').val(), forceRefresh: new Date()}).then(function(data){
			
			//{url: "commandes.afficherListArtCmdResultat", nb: 0, repartition: Array(5), totalTime: 1000, dateExtraction:{month: "NOVEMBER", year: 2017}}
			
			var extractionValues = data.map(function(e){return e.dateExtraction}).map(function(e){return e.dayOfMonth + "/" + e.monthValue + "/" + e.year})
			
			var avgValues = data.map(function(e){return e.avgTime})
			
			var newData = [{"label": urlAction, "data": avgValues, "fill": false, "borderColor": colors.random(), "lineTension": 0.1}]
			
			var labels = ["12/11/2017", "13/11/2017", "14/11/2017"]
			
			myChart.data.labels = extractionValues,
			myChart.data.datasets = newData
			myChart.update()
		})
	}
	
	$scope.updateTimes = function() {
		httpService.postData("/chart/updateAction", {server: 'lx01'}).then(function(data){
		})
	}

	// call when app is loaded
	angular.element(document).ready(function () {
		//$scope.updateLine("commandes.afficherListArtCmdResultat")
		$scope.updateLine("commandes.miseAJour")
		$scope.updateUrl()
	});
})

$(document).ready(function(){
	$(".datepicker").datepicker({ minDate: -60, maxDate: "+0M +0D" });
	$(".datepicker").datepicker("option", "dateFormat", "dd/mm/yy")
	var defautMax = new Date()
	var defautMin = new Date()
	defautMin.setMonth(defautMin.getMonth() - 1)
	$("#dateDebut").datepicker('setDate', defautMin);
	$("#dateFin").datepicker('setDate', defautMax);
})
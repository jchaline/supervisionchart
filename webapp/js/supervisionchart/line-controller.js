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
	
	//default value
	$scope.actionSort = "-avgTime"
		
	$scope.serveurs = [{libelle: 'lx01omega'}, {libelle: 'lx02omega'}, {libelle: 'lx03omega'}, {libelle: 'lx04omega'}]
	$scope.applications = []
	$scope.updateApplicationsList = function() {
		httpService.getData("/application/list", {forceRefresh: new Date()}).then(function(data){
			$scope.applications = data
		})
	}

	$scope.setServeurs = function(serveurs) {
		console.log(serveurs)
		$scope.serveurs = serveurs
	}

	$scope.updateUrl = function() {
		httpService.getData("/chart/listAction", {server: 'lx01omega', dateDebut: new $('#dateDebut').val(), dateFin: new $('#dateFin').val(), forceRefresh: new Date()}).then(function(data){
			$scope.actions = data
		})
	}

	$scope.updateLine = function(urlAction) {
		
		var serverList = "lx01omega, lx02omega, lx03omega"
		
		httpService.getData("/chart/evolutionUrl", {serverList: serverList, url: urlAction, dateDebut: new $('#dateDebut').val(), dateFin: new $('#dateFin').val(), forceRefresh: new Date()}).then(function(data){

			var datesValues = data['lx01omega'].map(function(e){return e.dateExtraction}).map(function(e){return e.dayOfMonth + "/" + e.monthValue + "/" + e.year})
			
			var avgValues = data['lx01omega'].map(function(e){return e.avgTime})
			
			var newData = [
				{"label": "lx01omega", "data": avgValues, "fill": false, "borderColor": colors.random(), "lineTension": 0.1},
				{"label": "lx02omega", "data": avgValues.map(function(e){return e * 0.5}), "fill": false, "borderColor": colors.random(), "lineTension": 0.1}]
			
			myChart.data.labels = datesValues,
			myChart.data.datasets = newData
			myChart.update()
		})
	}
	
	$scope.updateTimes = function() {
		httpService.postData("/chart/updateAction", {server: 'lx01omega'}).then(function(data){
		})
	}

	// call when app is loaded
	angular.element(document).ready(function () {
		//$scope.updateLine("commandes.afficherListArtCmdResultat")
		$scope.updateLine("commandes.miseAJour")
		$scope.updateUrl()
		
		$scope.updateApplicationsList()
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
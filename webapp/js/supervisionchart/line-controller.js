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
	
	function compareDate(d1, d2) {
		var a = d1.split('/')
		var b = d2.split('/')
		
		if (parseInt(a[2]) == parseInt(b[2])) {
			if (parseInt(a[1]) == parseInt(b[1])) {
				if (parseInt(a[0]) == parseInt(b[0])) {
					return 0
				} else if (parseInt(a[0]) < parseInt(b[0])) {
					return -1
				} else {
					return 1
				}
			} else if (parseInt(a[1]) < parseInt(b[1])) {
				return -1
			} else {
				return 1
			}
		} else if (parseInt(a[2]) < parseInt(b[2])) {
			return -1
		} else {
			return 1
		}
	}

	$scope.updateLine = function(urlAction) {
		
		var serverList = "lx01omega, lx02omega, lx03omega"
		
		httpService.getData("/chart/evolutionUrl", {serverList: serverList, url: urlAction, dateDebut: new $('#dateDebut').val(), dateFin: new $('#dateFin').val(), forceRefresh: new Date()}).then(function(data){
			var maplx1 = data['lx01omega'].reduce(function(map, e) {
				map[(e.dateExtraction.dayOfMonth + "/" + e.dateExtraction.monthValue + "/" + e.dateExtraction.year)] = e.avgTime;
				return map;
			}, {});

			var maplx2 = data['lx02omega'].reduce(function(map, e) {
				map[(e.dateExtraction.dayOfMonth + "/" + e.dateExtraction.monthValue + "/" + e.dateExtraction.year)] = e.avgTime;
				return map;
			}, {});
			
			for (key in maplx1) {
				if (!(key in maplx2)) {
					maplx2[key] = null
				}
			}

			for (key in maplx2) {
				if (!(key in maplx1)) {
					maplx1[key] = null
				}
			}
			
			var keyArray = Object.keys(maplx1).sort(compareDate)
			
			var values1 = []
			var values2 = []
			for (i=0; i<keyArray.length; i++) {
				values1.push(maplx1[keyArray[i]])
				values2.push(maplx2[keyArray[i]])
			}
			
			var newData = [
				{"label": "lx01omega", "data": values1, "fill": false, "borderColor": colors[4], "lineTension": 0.1},
				{"label": "lx02omega", "data": values2, "fill": false, "borderColor": colors[1], "lineTension": 0.1}]
			
			myChart.data.labels = keyArray,
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
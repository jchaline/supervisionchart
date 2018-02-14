var app = angular.module( "lineModule", ['datatables', 'ngResource'] )

app.controller("lineController", function( $scope, $rootScope, $interval, httpService ) {
	
	var colors = ["rgb(250, 0, 0)", "rgb(0, 163, 0)", "rgb(0, 0, 255)", "rgb(200, 150, 60)", "rgb(192, 75, 0)", "rgb(50, 200, 50)", "rgb(200, 50, 200)", "rgb(150, 50, 200)"]
	
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
		
		if ($scope.selectedApplication) {
			var serverList = $scope.selectedApplication.serveurs.map(s => s.libelle).join(',')

			httpService.getData("/chart/evolutionUrl", {serverList: serverList, url: urlAction, dateDebut: new $('#dateDebut').val(), dateFin: new $('#dateFin').val(), forceRefresh: new Date()}).then(function(data){
				
				//transformation de la liste d'action en map date => temps moyen
				var mapList = serverList.split(',').map(arrayId => {
					return data[arrayId].reduce(function(map, e) {
						map[(e.dateExtraction.dayOfMonth + "/" + e.dateExtraction.monthValue + "/" + e.dateExtraction.year)] = e.avgTime;
						return map;
					}, {});
				})
				
				//combinaison des maps, ajout des dates inexistantes sur chaque serveurs pour affichers les courbes sur le même graphique
				for (var i=0; i<mapList.length; i++) {
					for (var j=0; j<mapList.length; j++) {
						if (i != j) {
							for (key in mapList[i]) {
								if (!(key in mapList[j])) {
									mapList[j][key] = null
								}
							}
						}
					}
				}
				
				//liste des dates à affichers sur le graphique
				var keyArray = Object.keys(mapList[0]).sort(compareDate)
				
				//obtention des valeurs pour chaque dates et chaque server
				var valuesList = []
				for (var i=0; i<mapList.length; i++) {
					valuesList[i] = []
					for (var j=0; j<keyArray.length; j++) {
						valuesList[i].push(mapList[i][keyArray[j]])
					}
				}
				
				var i = 0
				var newData = valuesList.map(vl => {
					return {"label": i, "data": vl, "fill": false, "borderColor": colors[i++], "lineTension": 0.1}
				})
				
				myChart.data.labels = keyArray,
				myChart.data.datasets = newData
				myChart.update()
			})
		}
			
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
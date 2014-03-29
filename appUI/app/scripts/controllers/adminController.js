'use strict';
/*
 * Controller for admin view. Responsible for showing global
 * statistics, adding cabins and overview of cabins.
 */
app.controller('adminViewController', function ($scope, $location,$routeParams,  api, $log) {
	$scope.view = 0;
	
	$scope.$on('viewCabin', function(event, id) {
		$scope.setCabinView(id);
	
	});
	
	$scope.setCabinView = function(id) {
		$log.info(id);
		$scope.view = 1;
		$scope.$broadcast('retrieveCabin', id);
	};
	
	
	init();
	function init() {
		if($routeParams.id) {
			$scope.id = $routeParams.id;
		}
	};
});

/*
 * Table controller for overview of cabins. Resposible
 * for populating table with cabins.
 */
app.controller('cabinTableController', function ($scope, $location, $routeParams, cabinService, api, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.error = "";
	$scope.setPage = function(page) {
		$scope.getCabins(page-1);
	};


	$scope.getCabins = function(page) {
		
		cabinService.getCabins(page, $scope.itemsPerPage)
		.success(function (cabins) {
			$scope.cabins = cabins.data;
			
			$scope.totalItems = cabins.totalItems;
			
		})
		.error(function (error) {
			$log.info("problem");
			$scope.error = error;
		});
	};

	
	$scope.viewCabin = function( cabinId ) {
		$scope.$emit('viewCabin', cabinId);
	};
	

	
	init();
	function init() {
			var page = $routeParams.page;
			if(page) {
				$scope.currentPage = page;
				$scope.getCabins(page-1);
			}
			else {
				$scope.getCabins(0);
			}
	};
});

/*
 * Table controller for overview of bookings for a given cabin 
 */
app.controller('cabinDetailsController', function ($scope, $location, $routeParams, cabinService, api, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.error = "";
	$scope.id;
	
	
	$scope.setPage = function(page) {
		$scope.getCabinDetails(page-1, $scope.id);
	};
	
	
	$scope.$on('retrieveCabin', function(event, id) {
		$scope.id = id;
		$scope.getCabinDetails(0, id);
		 
	});
	
	
	$scope.getCabinDetails = function(page, cabinId) {
		$log.info("fgdfgdfg")
		cabinService.getCabinDetails(page, $scope.itemsPerPage, cabinId)
		.success(function (details) {
			$scope.cabinDetails = details.data;
			$scope.totalItems = details.totalItems;
			
		})
		.error(function (error) {
			$log.info("problem");
			$scope.error = error;
		});
	};

	
	$scope.init = function(id) {
		if (id) {
			 $scope.id = id;
			 getCabinDetails(0, id);
			
		}
	  };
});

app.controller('cabinFormController', function ($scope, $location, $routeParams, cabinService, api, $log) {
	$scope.show = false;
	
	$scope.addCabin = function(newCabin) {
		cabinService.postCabin(newCabin)
		.success(function (data) {
			$log.info("posted");
		})
		.error(function (error) {
			$log.info("not posted" + error.message);
		});
	};
	
	
	$scope.showCabinForm = function() {
		$scope.show = !$scope.show
	};
});

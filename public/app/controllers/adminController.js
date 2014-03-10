app.controller('adminViewController', function ($scope, $location,  api, $log) {
	$scope.view = 0;
	
	$scope.$on('viewCabin', function(event, id) {
		$scope.view = 1;
		$scope.$broadcast('retrieveCabin', id);
	
	});
	
	
});

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

app.controller('cabinDetailsController', function ($scope, $location, $routeParams, cabinService, api, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.error = "";
	
	$scope.setPage = function(page) {
		$scope.getCabinDetails(page-1, $scope.id);
	};
	$scope.id;
	
	
	$scope.$on('retrieveCabin', function(event, id) {
		$scope.id = id;
		$scope.getCabinDetails(0, id);
	});
	
	$scope.getCabinDetails = function(page, cabinId) {
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

	
	init();
	function init() {
	};
});



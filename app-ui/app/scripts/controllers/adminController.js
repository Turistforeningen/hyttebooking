'use strict';


/**
 * @ngdoc object
 * 
 * @name dntApp.controller:adminViewController
 * @description Controller for admin view. Responsible for showing global
 * statistics, adding cabins and overview of cabins.
 * 
 */
angular.module('dntApp').controller('adminViewController',['$scope', '$location','$routeParams',  'api', '$log',
                                                           function ($scope, $location,$routeParams,  api, $log) {
	$scope.view = 0;

	$scope.$on('viewCabin', function(event, id) {
		$scope.setCabinView(id);

	});

	$scope.setCabinView = function(id) {
		$log.info(id);
		$scope.view = 1;
		$scope.$broadcast('retrieveCabin', id);
	};


	
	function init() {
		if($routeParams.id) {
			$scope.id = $routeParams.id;
		}
	}
	init();
}]);


/**
 * @ngdoc object
 * 
 * @name dntApp.controller:cabinTableController
 * @description Table controller for overview of cabins. Responsible
 * for populating table with cabins.
 * 
 */
angular.module('dntApp').controller('cabinTableController', ['$scope', '$location', '$routeParams', 'cabinService', 'api', '$log',
                                                             function ($scope, $location, $routeParams, cabinService, api, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.error = '';
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
			$log.info('problem');
			$scope.error = error;
		});
	};


	$scope.viewCabin = function( cabinId ) {
		$scope.$emit('viewCabin', cabinId);
	};




	function init() {
		var page = $routeParams.page;
		if(page) {
			$scope.currentPage = page;
			$scope.getCabins(page-1);
		}
		else {
			$scope.getCabins(0);
		}
	}
	init();
}]);


/**
 * @ngdoc object
 * 
 * @name dntApp.controller:cabinDetailsController
 * @description  Table controller for overview of bookings for a given cabin .
 * 
 */
angular.module('dntApp').controller('cabinDetailsController', ['$scope', '$location', '$routeParams', 'cabinService', 'api', '$log',
                                                               function ($scope, $location, $routeParams, cabinService, api, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.error = '';
	$scope.id =-1;


	
	$scope.getDetails = function(page, cabinId) {
		cabinService.getCabinDetails(page, $scope.itemsPerPage, cabinId)
		.success(function (details) {
			$scope.cabinDetails = details.data;
			$scope.totalItems = details.totalItems;

		})
		.error(function (error) {
			$log.info('problem');
			$scope.error = error;
		});
	};

	$scope.$on('retrieveCabin', function(event, id) {
		$scope.id = id;
		$scope.getDetails(0, id);

	});
	
	$scope.setPage = function(page) {
		$scope.getDetails(page-1, $scope.id);
	};
	
	$scope.init = function(id) {
		if (id) {
			$scope.id = id;
			$scope.getDetails(0, id);

		}
	};
}]);


angular.module('dntApp').controller('cabinFormController', ['$scope', '$location', '$routeParams', 'cabinService', 'api', '$log',
                                                            function ($scope, $location, $routeParams, cabinService, api, $log) {
	$scope.show = false;

	$scope.addCabin = function(newCabin) {
		cabinService.postCabin(newCabin)
		.success(function (data) {
			$log.info('posted');
		})
		.error(function (error) {
			$log.info('not posted' + error.message);
		});
	};


	$scope.showCabinForm = function() {
		$scope.show = !$scope.show;
	};
}]);

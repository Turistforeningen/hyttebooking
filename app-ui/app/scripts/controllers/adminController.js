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

	function init() {
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
		.then(function(data){
			$scope.cabins 		= data.data;
			$scope.totalItems 	= data.totalItems;
		},
		function(errorMessage){
			$scope.error=errorMessage;
		});
	};


	$scope.viewCabin = function( cabinId ) {
		$location.path('/admin/cabin/' + cabinId);
	};

	$scope.showCabinForm = function() {
		$scope.$broadcast('event:showCabinForm');
	};

	$scope.$on('event:postCabinSuccess', function(event) {
		$scope.getCabins($scope.page-1);
	});
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
angular.module('dntApp').controller('cabinDetailsController', ['$scope','$modal', '$location', '$routeParams','bookingService' ,'cabinService', '$log',
                                                               function ($scope, $modal, $location, $routeParams,bookingService, cabinService, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.error = '';
	$scope.id =-1;
	
	$scope.priceCategories = {};

	
	$scope.getDetails = function(page, cabinId) {
		cabinService.getCabinDetails(page, $scope.itemsPerPage, cabinId)
		.then(function(data){
			$scope.cabinBookings = data.bookingList.data;
			$scope.totalItems = data.bookingList.totalItems;
			$scope.cabinDetails = data.cabin;
			if($scope.cabinDetails.cabinType == 'small') {
				$scope.cabinDetails.nrOfBeds = 'none';
			}
			$scope.getPrices(cabinId);
		}, function(errorMessage){
			$scope.error=errorMessage;
		});
	};
	
	$scope.getPrices = function(cabinId) {
		cabinService.getPrices(cabinId)
		.then(function(data){
				$scope.priceCategories = data;
		}, function(errorMessage){
			$scope.error=errorMessage;
		});
	};
	
	$scope.removePrice = function(cabinId, price) {
		cabinService.removePriceFromCabin(cabinId, price.id)
		.then(function(data){
			var index = $scope.priceCategories.indexOf(price);
			$scope.priceCategories.splice(index, 1);
				
		}, function(errorMessage){
			$scope.error=errorMessage;
		});
	};
	
	$scope.addPrice = function(cabinId, priceData) {
		$log.info("blir kalt");
		cabinService.addPriceFromCabin(cabinId, priceData)
		.then(function(data){
				//id of new price returned from backend
				priceData.id = data.id;
				$scope.priceCategories.push(priceData);
				$scope.priceForm = {};
		}, function(errorMessage){
			$scope.error=errorMessage;
		});
	};
	
	$scope.setPage = function(page) {
		$scope.getDetails(page-1, $scope.id);
	};
	$scope.open = function (orderId) {
		bookingService.getOrderSummary(orderId)
		.then(function(order){
			$scope.openDialog('/views/receiptModal.html', order);
		});
	};
	
	$scope.cancelOrder = function (order) {
		$scope.openDialog('/views/cancelConfirmModal.html', null).result.then(function () {
			bookingService.adminCancelOrder(order.id)
			.then(function(data){
				order.status = 2;
			},
			function(error){
				$log.info(error);
				$scope.status = 'not found' + error.message;
			});
		});
	};
	
	$scope.openDialog = function (url, data) {
		var modalInstance = $modal.open({
			templateUrl: url,
			controller: 'ModalInstanceCtrl',
			resolve: {
		        item: function () {
		          return data;
		        }
		    }
		});
		return modalInstance
	};
	
	function init() {
		var id = $routeParams.id;
		var page = $routeParams.page;
		if(page && id) {
			$scope.id = id;
			$scope.currentPage = page;
			$scope.getDetails(page-1, id);
			//$scope.getPrices(id);
		}
		else if(id) {
			$scope.id = id;
			$scope.getDetails(0, id);
			//$scope.getPrices(id);
		}
	}
	init();
}]);


angular.module('dntApp').controller('cabinFormController', ['$scope', '$location', '$routeParams', 'cabinService', '$log',
                                                            function ($scope, $location, $routeParams, cabinService, $log) {
	$scope.show = false;

	$scope.addCabin = function(newCabin) {
			cabinService.postCabin(newCabin).then(function(data){
			$scope.$emit('event:postCabinSuccess');
		},
		function(error){
			$scope.errorMessage=error.message;
		});
	};

	$scope.$on('event:showCabinForm', function(event) {
		$scope.show = true;
	});
}]);

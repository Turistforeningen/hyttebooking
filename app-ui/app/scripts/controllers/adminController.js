'use strict';


/**
 * @ngdoc object
 * 
 * @name dntAdminApp.controller:adminViewController
 * @description Controller for admin view. Responsible for showing global
 * statistics, adding cabins and overview of cabins.
 * 
 */
angular.module('dntAdminApp').controller('adminViewController',['$scope', '$location','$routeParams',  'api', '$log',
                                                           function ($scope, $location,$routeParams,  api, $log) {

	function init() {
	}
	init();
}]);


/**
 * @ngdoc object
 * 
 * @name dntAdminApp.controller:cabinTableController
 * @requires dntCommon.cabinService
 * @description Table controller for overview of cabins. Responsible
 * for populating table with cabins.
 * 
 * 
 */
angular.module('dntAdminApp').controller('cabinTableController', ['$scope', '$location', '$routeParams', 'cabinService', '$log',
                                                             function ($scope, $location, $routeParams, cabinService, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 0;
	$scope.itemsPerPage = 10;
	$scope.error = '';
	$scope.setPage = function(page) {
		$scope.getCabins(page-1);
	};

	/**
     * @ngdoc method
     * @name dntAdminApp.object#getCabins
	 * @methodOf dntAdminApp.controller:cabinTableController
     * @param {Number} page What page of cabins to request. Each page contain 10 cabins
     * @description Method utilize cabinService to request a page of cabins from the back end, and
     * handles the resolved or rejected promise
     */
	$scope.getCabins = function(page) {
		cabinService.getCabins(page, $scope.itemsPerPage)
		.then(function(data){
			$scope.cabins 		= data.data;
			$log.info($scope.cabins);
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
 * @name dntAdminApp.controller:cabinDetailsController
 * @requires dntCommon.cabinService
 * @requires dntCommon.bookingService
 * @requires ui.bootstrap.$modal
 * @description Controller for overview of bookings for a given cabin, general cabin information,
 * viewing prices for the cabin. The `cabinDetailsController` supports different actions a admin might make, like
 * adding a price, removing a price, sending email to customer, viewing a reciept and cancelling a booking
 * 
 */
angular.module('dntAdminApp').controller('cabinDetailsController', ['$scope','$modal', '$location', '$routeParams','bookingService' ,'cabinService', '$log',
                                                               function ($scope, $modal, $location, $routeParams,bookingService, cabinService, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 0;
	$scope.itemsPerPage = 10;
	$scope.error = '';
	$scope.id =-1;
	
	$scope.priceCategories = [];

	/**
     * @ngdoc method
     * @name dntAdminApp.object#getDetails
	 * @methodOf dntAdminApp.controller:cabinDetailsController
     * @param {Number} page 	What page of bookings for cabin to request. Each page contain 10 bookings.
     * @param {Number} cabinId 	Id of cabin to request bookings from.
     * @description Method utilize cabinService to request a page of bookings for the cabin from the back end, and
     * handles the resolved or rejected promise. A resolved promise's data are put in the scope and will
     * be displayed in the view.
     */
	$scope.getDetails = function(page, cabinId) {
		cabinService.getCabinDetails(page, $scope.itemsPerPage, cabinId)
		.then(function(data){
			$log.info(data);
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
	
	/**
     * @ngdoc method
     * @name dntAdminApp.object#getPrices
	 * @methodOf dntAdminApp.controller:cabinDetailsController
     * @param {Number} cabinId 	Id of cabin to request the price categories from.
     * @description Method utilize cabinService to request a all prices currently active for cabin, and
     * handles the resolved or rejected promise. A resolved promise's data are put in the scope and will
     * be displayed can be displayed in a view (I.E table on the admin page).
     */
	$scope.getPrices = function(cabinId) {
		cabinService.getPrices(cabinId)
		.then(function(data){
				$scope.priceCategories = data;
		}, function(errorMessage){
			$scope.error=errorMessage;
		});
	};
	
	/**
     * @ngdoc method
     * @name dntAdminApp.object#removePrice
	 * @methodOf dntAdminApp.controller:cabinDetailsController
     * @param {Number} cabinId 		Id of cabin to request removal of price to back end.
     * @param {JSON object} price 	An object containing the a price category. I.e "id", "memberPrice", "nonMemberPrice" etc.
     * @description Method utilize cabinService to request a price to be removed as a price category for the cabin
     * owning the cabinId. When the promise is resolved, the price is removed from the array of price categories.
     */
	$scope.removePrice = function(cabinId, price) {
		cabinService.removePriceFromCabin(cabinId, price.id)
		.then(function(data){
			var index = $scope.priceCategories.indexOf(price);
			$scope.priceCategories.splice(index, 1);
				
		}, function(error){
			$scope.error=error.message;
		});
	};
	
	/**
     * @ngdoc method
     * @name dntAdminApp.object#addPrice
	 * @methodOf dntAdminApp.controller:cabinDetailsController
     * @param {Number} cabinId 	Id of cabin to request a price to be added.
     * @param {JSON object} priceData 	An object containing the new price category.
     * @description Method utilize cabinService to request a price to be added as a price category to a cabin.
     *  When the promise is resolved, the price is either pushed into the array of priceCategories or overwrite
     *  the existing price categories, depending on whether the cabin is a large or small one. (Small cabins should
     *  only have one price category. The priceForm is then wiped. (A table displaying this data will
     *  now reflect the state of the backend).
     */
	$scope.addPrice = function(cabinId, priceData) {
		cabinService.addPriceFromCabin(cabinId, priceData)
		.then(function(data){
				//id of new price returned from backend
				priceData.id = data.id;
				if($scope.cabinDetails.cabinType == 'large') {
					$scope.priceCategories.push(priceData);
				}
				else {
					$scope.priceCategories[0] = priceData;
				}
				$scope.priceForm = {};
		}, function(errorMessage){
			$scope.error=errorMessage;
		});
	};
	
	$scope.setPage = function(page) {
		$scope.getDetails(page-1, $scope.id);
	};
	
	/**
     * @ngdoc method
     * @name dntAdminApp.object#cancelOrder
	 * @methodOf dntAdminApp.controller:cabinDetailsController
     * @param {JSON object} order 	An object containing the order data to be cancelled.
     * @description Method will try to delete a order in the back end. It first
     * opens a modal to let the admin confirm the cancellation of a customers order.
     * Then if cancellation was confirmed, bookingService's adminCancelOrder is used to delete an order. 
     * If the promise is not rejected but resolved the status
     * of the booking will be set to cancelled, to reflect the back end. 
     */
	$scope.cancelOrder = function (order) {
		if(order.adminAbleToCancel) {
			$scope.openDialog('/views/cancelConfirmModal.html', null).result.then(function () {
				bookingService.adminCancelOrder(order.id)
				.then(function(data){
					order.status = 2;
				},
				function(error){
					$log.info(error);
					$scope.error = 'not found' + error.message;
				});
			});
		}
		else {
			$scope.errorMessage = "Kan ikke kansellere denne bookingen";
		}
		
	};
	
	//receipt
	$scope.open = function (orderId) {
		bookingService.getOrderSummary(orderId)
		.then(function(ord){
			$log.info(ord);
			var modalInstance = $scope.openDialog('/views/receiptModal.html', ord);
		});
	}
	
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
	
	/**
     * @ngdoc method
     * @name dntAdminApp.object#init
	 * @methodOf dntAdminApp.controller:cabinDetailsController
     * @description init method for controller. Run at initialization of controller 
     */
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

/**
 * @ngdoc object
 * 
 * @name dntAdminApp.controller:cabinFormController
 * @requires dntCommon.cabinService
 * @description Controller for form adding a cabin to the system. It has a method submitting a 
 * cabin to the server, and handles errors linked to posting a cabin.
 * 
 */
angular.module('dntAdminApp').controller('cabinFormController', ['$scope', '$location', '$routeParams', 'cabinService', '$log',
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

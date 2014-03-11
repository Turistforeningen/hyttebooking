/*
 * Controller for the ordersView. Sends a get request for orderHistory to the server.
 * Methods for getting and cancelling bookings.
 */
app.controller('orderController', function ($scope, $location, $routeParams, ordersService, api, $log) {
	$scope.currentPage = 1;
	$scope.totalItems = 10;
	$scope.itemsPerPage = 10;
	$scope.lang= {oe: 'fødselsdag'};
	$scope.setPage = function(page) {
		$scope.getOrders(page-1);
	};


	$scope.getOrders = function(page) {
		$log.info($scope.itemsPerPage);
		ordersService.getOrders(page, $scope.itemsPerPage)
		.success(function (userOrders) {
			$scope.orders = userOrders.data;
			$scope.totalItems = userOrders.totalItems;
			
		})
		.error(function (error) {
			$log.info("problem");
			$scope.status = 'unable to load customer data' + error.message;
		});
	};


	$scope.cancelOrder = function (order) {
		ordersService.cancelOrder(order.id)
		.success(function (data) {
			var index = $scope.orders.indexOf(order)
			$scope.orders.splice(index, 1);
		})
		.error(function (error) {
			$scope.status = 'not found' + error.message;
		});
	};



	init();
	function init() {
			var page = $routeParams.page;
			if(page) {
				$scope.currentPage = page;
				$scope.getOrders(page-1);
			}
			else {
				$scope.getOrders(0);
			}
			
		
	};
});

/*
 * Controller for testView.
 */
app.controller('testController', function ($scope) {
	
	  
    init();

    function init() {
       
    }

});

/*
 * Controller for bookingView. Possible to add danger and sucess alert to the view
 * by using the method addAlert. The method postBooking uses the ordersService to 
 * post the booking to the server.
 */
app.controller('bookingController', function ($scope, ordersService) {
	
	$scope.addAlert = function(type, msg) {
		$scope.alerts = [{type: type, msg: msg}];
		
	};

	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};
	
    $scope.postBooking = function(booking) {
    	
		// Temporary code
		//var startD = booking.dates.startDate;
		//$scope.start = startD.substring(0, 10);
		$scope.start = JSON.stringify(booking.dates.startDate).substring(1, 11);
		$scope.slutt = JSON.stringify(booking.dates.endDate).substring(1, 11);
		booking.dates.startDate=JSON.stringify(booking.dates.startDate).substring(1, 11);
		booking.dates.endDate=JSON.stringify(booking.dates.endDate).substring(1, 11);
		// End temporary code
		
    	ordersService.postOrder(booking)
		.success(function (success) {
			$scope.addAlert('success', success.message);
		})
		.error(function (error) {
			$scope.addAlert('danger', error.message);
		});
    };
    init();
    
    function init() {
       
    }

});

/*
 * The authController is the controller of authView and is responsible for 
 * sending user credentials to server and take care of a authentication token return by the server.
 * 
 */
app.controller('authController', function ($log, $scope, $location, $cookieStore, authorization, api) {
	
	
	
	
	$scope.login = function (credentials) {
		
		var success = function (data) {
			
			$scope.SignInText = credentials.emailAdress;
			
			var token = data.authToken;
			api.init(token);
			$cookieStore.put('token', token);
			$location.path('/');
		};

		var error = function () {
			
		};
		authorization.login(credentials).success(success).error(error);
	};
	
$scope.logout = function () {
		
		var success = function (data) {
			
			$cookieStore.remove('token');
			$location.path('/');
		};

		var error = function (error) {
			$log.info(error);
		};
		authorization.logout().success(success).error(error);
	};
});

/*
 * Small controller used by navbar in indexAngular.html to set 
 * active tab.
 */
function HeaderController($scope, $location, $log) 
{ 
    $scope.isActive = function (viewLocation) { 
    	
        return viewLocation === $location.path();
    };
}
/*
 * Controller for the ordersView. Sends a get request for orderHistory to the server.
 * Methods for getting and cancelling bookings.
 */
app.controller('orderController', function ($scope, $location, ordersService, api) {

	$scope.getBookings = function () {
		var success = function (data) {
			$scope.orders = data;
		};

		var error = function () {
			$scope.status = 'unable to load customer data' + error.message;
		};
		
		api.getBookings().success(success).error(error);
	};


	$scope.getOrders = function() {
		ordersService.getOrders()
		.success(function (userOrders) {
			$scope.orders = userOrders;
		})
		.error(function (error) {
			$scope.status = 'unable to load customer data' + error.message;
		});
	};


	$scope.cancelOrder = function (order) {
		ordersService.cancelOrder(order.id)
		.success(function () {
			var index = $scope.orders.indexOf(order)
			$scope.orders.splice(index, 1);
		})
		.error(function (error) {
			$scope.status = 'not found' + error.message;
		});
	};



	init();
	function init() {
		
			$scope.getOrders();
		
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
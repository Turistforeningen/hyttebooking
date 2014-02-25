app.controller('orderController', function ($scope,$routeParams, $location, ordersService, api) {

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

app.controller('testController', function ($scope) {
	
	  
    init();

    function init() {
       
    }

});

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
			$scope.addAlert('success', "det virket!");
		})
		.error(function (error) {
			$scope.addAlert('danger', "virket ikke!");
		});
    };
    init();
    
    function init() {
       
    }

});

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

function HeaderController($scope, $location, $log) 
{ 
    $scope.isActive = function (viewLocation) { 
    	
        return viewLocation === $location.path();
    };
}
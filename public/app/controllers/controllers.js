app.controller('orderController', function ($scope,$routeParams, ordersService) {




	$scope.getOrders = function(userId) {
		ordersService.getOrders(userId)
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
		var userId = ($routeParams.customerID) ? parseInt($routeParams.customerID) : -1;
		if (userId > -1) {
			$scope.getOrders(userId);
		} 
	};
});

app.controller('testController', function ($scope) {

    init();

    function init() {
       
    }

});

app.controller('bookingController', function ($scope) {

    init();
    
    function init() {
       
    }

});

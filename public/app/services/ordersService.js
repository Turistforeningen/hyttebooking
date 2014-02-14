app.service('ordersService', ['$http' , function ($http) {
	
    this.getOrders = function (id) {
        var url = '/api/'+id+'/bookings';
        return $http.get(url);
       
    };

    this.cancelOrder = function (id) {
    	var url = '/api/bookings/' + id;
        return $http.delete(url);
    };
}]);
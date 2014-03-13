/*
 * Service with functions used to interface client with server.
 */
app.service('ordersService', function ($http, $log) {
	
	
    this.getOrders = function (page, pageSize) {
        var url = '/api/bookings?page=' + page + "&size=" + pageSize;
        return $http.get(url);
       
    };

    this.cancelOrder = function (id) {
    	var url = '/api/bookings/' + id;
        return $http.delete(url);
    };
    
    this.postOrder = function(data) {
    	var url = '/api/bookings/'
    	return $http.post(url, data);
    };
    
    this.getPrices = function (id) {
    	var url = '/api/cabins/' + id +'/prices';
        return $http.get(url);
    };
});


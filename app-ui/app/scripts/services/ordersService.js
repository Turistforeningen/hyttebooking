'use strict';

/*
 * Service with functions used to interface client with server.
 */
/**
 * @ngdoc service 
 * @name dntApp.ordersService
 * @description Service with functions used to interface client with server. Mainly used
 * to post a bookings, get a users booking list, get the current prices for a booking and 
 * for payment (SHOULD PROBABLY MAKE ANOTHER SERVICE FOR THE FORMER)
 * All data returned from the server should be json strings.
 * @requires $http 
**/
angular.module('dntApp').service('ordersService', ['$http','$log', function ($http, $log) {

	/**
     * @ngdoc method
     * @name dntApp.service#getOrders
     * @methodOf dntApp.ordersService
     * @returns {json} A list containing a subset of a logged in users bookings.
     */
	this.getOrders = function (page, pageSize) {
		var url = '/api/bookings?page=' + page + '&size=' + pageSize;
		return $http.get(url);

	};
	
	/**
     * @ngdoc method
     * @name dntApp.service#cancelOrder
     * @methodOf dntApp.ordersService
     * @returns {json} An answer containing status and message from the server.
     */
	this.cancelOrder = function (id) {
		var url = '/api/bookings/' + id;
		return $http.delete(url);
	};

	/**
     * @ngdoc method
     * @name dntApp.service#postOrder
     * @methodOf dntApp.ordersService
     * @returns {json} An answer containing status and message from the server.
     */
	this.postOrder = function(data) {
		$log.info(data + 'THIS ID SATA');
		$log.info(data);
		var url = '/api/bookings/';
		return $http.post(url, data);
	};
	
	/**
     * @ngdoc method
     * @name dntApp.service#postOrder
     * @methodOf dntApp.ordersService
     * @returns {json} A array containing different guesttypes and price accepted at cabin specified by cabinId
     */
	this.getPrices = function (cabinId) {
		var url = '/api/cabins/' + cabinId +'/prices';
		return $http.get(url);
	};

	/**
     * @ngdoc method
     * @name dntApp.service#startPayment
     * @methodOf dntApp.ordersService
     * @returns {json} Containing properties for redirectUrl and transactionId
     */
	this.startPayment = function(id) {
		var url = '/api/bookings/' + id +'/payment';
		return $http.get(url);
	};

	/**
     * @ngdoc method
     * @name dntApp.service#authenticatePayment
     * @methodOf dntApp.ordersService
     * @returns {json} containing status and message
     */
	this.authenticatePayment = function(paymentId, response) {
		var url = '/api/payment/authenticate';
		var data = {'responseCode' :response, 'transactionId' : paymentId};
		return $http.post(url, data);
	};
	
	this.getAvailability = function(cabinId, startDate, endDate) {
		var url = '/api/cabins/'+cabinId+'/availability?startDate='+startDate+'&endDate='+endDate;
		return $http.get(url);
	};
}]);


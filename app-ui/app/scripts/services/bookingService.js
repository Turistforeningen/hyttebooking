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
angular.module('dntApp').factory('bookingService', ['$http', '$q','$log', function ($http,$q, $log) {

	
	return {
	
	/**
     * @ngdoc method
     * @name dntApp.service#getOrders
     * @methodOf dntApp.bookingService
     * @returns {json} A list containing a subset of a logged in users bookings.
     */
	getOrders: function(page, pageSize) {
		var deferred = $q.defer();
		var url = '/api/bookings?page=' + page + '&size=' + pageSize;
		$http.get(url).success(function(data){
			//Passing data to deferred's resolve function on successful completion
			deferred.resolve(data);
		}).error(function(error){

			//Sending a friendly error message in case of failure
			deferred.reject(error);
		});
		return deferred.promise;
	},
	
	getOrderSummary: function(bookingId) {
		var deferred = $q.defer();
		var url = '/api/bookings/' + bookingId;
		$http.get(url).success(function(data){
			//Passing data to deferred's resolve function on successful completion
			deferred.resolve(data);
		}).error(function(error){

			//Sending a friendly error message in case of failure
			deferred.reject(error);
		});
		return deferred.promise;
	},
	
	/**
     * @ngdoc method
     * @name dntApp.service#cancelOrder
     * @methodOf dntApp.bookingService
     * @returns {json} An answer containing status and message from the server.
     */
	cancelOrder: function(id) {
		var deferred = $q.defer();
		var url = '/api/bookings/' + id;
		$http.delete(url).success(function(data){
			deferred.resolve(data);
		}).error(function(error){

			deferred.reject(error);
		});
		return deferred.promise;
	},
	
	/**
     * @ngdoc method
     * @name dntApp.service#adminCancelOrder
     * @methodOf dntApp.bookingService
     * @returns {json} An answer containing status and message from the server.
     */
	adminCancelOrder: function(id) {
		var deferred = $q.defer();
		var url = '/api/admin/bookings/' + id;
		$http.delete(url).success(function(data){
			deferred.resolve(data);
		}).error(function(error){

			deferred.reject(error);
		});
		return deferred.promise;
	},
	
	/**
     * @ngdoc method
     * @name dntApp.service#postOrder
     * @methodOf dntApp.bookingService
     * @returns {json} An answer containing status and message from the server.
     */
	postOrder: function(data) {
		var deferred = $q.defer();
		var url = '/api/bookings/';
		$http.post(url, data).success(function(data){
			deferred.resolve(data);
		}).error(function(error){

			deferred.reject(error);
		});
		return deferred.promise;
	},
	//should be in cabinService --delete later (refactor dntBookingModule)
	/**
     * @ngdoc method
     * @name dntApp.service#postOrder
     * @methodOf dntApp.bookingService
     * @returns {json} A array containing different guesttypes and price accepted at cabin specified by cabinId
     */
	getPrices: function(cabinId) {
		var deferred = $q.defer();
		var url = '/api/cabins/' + cabinId +'/prices';
		$http.get(url).success(function(data){
			deferred.resolve(data);
		}).error(function(error){

			deferred.reject(error);
		});
		return deferred.promise;
	},
	
	/**
     * @ngdoc method
     * @name dntApp.service#startPayment
     * @methodOf dntApp.bookingService
     * @returns {json} Containing properties for redirectUrl and transactionId
     */
	startPayment: function(id) {
		var deferred = $q.defer();
		var url = '/api/bookings/' + id +'/payment';
		$http.get(url).success(function(data){
			deferred.resolve(data);
		}).error(function(error){

			deferred.reject(error);
		});
		return deferred.promise;
	},
	/**
     * @ngdoc method
     * @name dntApp.service#authenticatePayment
     * @methodOf dntApp.bookingService
     * @returns {json} containing status and message
     */
	authenticatePayment: function(paymentId, response) {
		var deferred = $q.defer();
		var data = {'responseCode' :response, 'transactionId' : paymentId};
		var url = '/api/payment/authenticate';
		$http.post(url, data).success(function(data){
			deferred.resolve(data);
		}).error(function(error){

			deferred.reject(error);
		});
		return deferred.promise;
	},
	
	getAvailability: function(cabinId, startDate, endDate) {
		var deferred = $q.defer();
		var url = '/api/cabins/'+cabinId+'/availability?startDate='+startDate+'&endDate='+endDate;
		$http.get(url).success(function(data){
			deferred.resolve(data);
		}).error(function(error){

			deferred.reject(error);
		});
		return deferred.promise;
	}
	};
}]);


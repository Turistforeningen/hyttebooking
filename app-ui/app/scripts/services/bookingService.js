'use strict';

/*
 * Service with functions used to interface client with server.
 */
/**
 * @ngdoc service 
 * @name dntApp.bookingService
 * @description Service with functions used to interface client with server. Mainly used
 * to post a bookings, get a users booking list, get the current prices for a booking and 
 * for payment.
 * @requires $http 
 * @requires $q
**/
angular.module('dntApp').factory('bookingService', ['$http', '$q','$log', function ($http,$q, $log) {

	
	return {
	
	/**
     * @ngdoc method
     * @name dntApp.service#getOrders
     * @methodOf dntApp.bookingService
     * @param {Number} page What page of orders.
     * @param {Number} pageSize How big the resultset should be.
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
     * @param {Number} id bookingId of the booking to request cancelled.
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
     * @param {Number} id bookingId of the booking to request cancelled.
     * @returns {JSON object} An answer containing status and message from the server.
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
     * @param {JSON object} data JSON object containing booking data.
     * @returns {JSON object} An answer containing status and message from the server.
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
     * @name dntApp.service#getPrices
     * @methodOf dntApp.bookingService
     * @param {Number} cabinId id of cabin to request prices for.
     * @returns {JSON object} A array containing different guesttypes and price accepted at cabin specified by cabinId
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
     * @param {Number} id id of booking to setup payment for.
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
     * @param {Number} paymentId transaction id
     * @param {String} response code from nets
     * @returns {JSON object} containing status and message
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


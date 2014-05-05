'use strict';

/**
 * @ngdoc service 
 * @name dntApp.bookingService
 * @description Service with functions used as an interface between the client and the server. Mainly used
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
     * @description Gets an JSON array containing a list of the users bookings.
     * @returns {Array} A list containing a subset of a logged in users bookings. (If promise has been resolved)
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
	
	
	/**
     * @ngdoc method
     * @name dntApp.service#getOrderSummary
     * @methodOf dntApp.bookingService
     * @param {Number} bookingId What booking to get the orderSummary for.
     * @description Gets all available information about a booking from the back end.
     * @returns {JSON objecet} A object with details about a booking. (If promise is resolved)
     */
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
     * @description `cancelOrder` will request a the server to cancel a booking. If successful promise
     * is resolved, to late to cancel, not users booking etc the promise is rejected.
     * @returns {json} An answer containing status and message from the server. (If promise has been resolved)
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
     * @description `adminCancelOrder` request server to cancel a booking. Similar to cancelOrder 
     * but different rules at the backend. Promise is resolved if successful, and rejected if something went
     * wrong.
     * @returns {JSON object} An answer containing status and message from the server.(If promise has been resolved)
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
     * @description A booking is posted to the back end using this method. If a booking is successful 
     * the promise is resolved, else its rejected. A resolved promise contain the bookingId.
     * @returns {JSON object} An answer containing status and message from the server. (If promise has been resolved)
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
	/**
     * @ngdoc method
     * @name dntApp.service#getPrices
     * @methodOf dntApp.bookingService
     * @param {Number} cabinId id of cabin to request prices for.
     * @description `getPrices` request back end for the price matrix belonging to the cabin with `cabinId`.
     * @returns {JSON object} A array containing different guesttypes and price accepted at cabin specified by cabinId.
     * (If promise has been resolved)
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
     * @description `startPayment` request a setup of payment at the backend and will return a redirectUrl the front
     * end can redirect to for payment.
     * @returns {JSON object} Containing properties like redirectUrl and transactionId (If promise has been resolved)
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
     * @description `authenticatePayment` should use this method after redirecting back from Netaxept. The
     * back end will check with Netaxept to see if booking has been paid for.
     * @returns {JSON object} An object containing status and message (If promise has been resolved)
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
	
	/**
     * @ngdoc method
     * @name dntApp.service#getAvailability
     * @methodOf dntApp.bookingService
     * @param {Number} cabinId of cabin to request availability for.
     * @param {Date} startDate of requested availability array.
     * @param {Date} endDate  of requested availability array.
     * @description `getAvailability` returns a promise which is resolved or rejected depending response
     * from the back end. Contains an availability array showing beds/cabin taken for days between
     * `startDate` and `endDate`.
     * @returns {Array} A integer array showing beds taken from startDate to endDate (If promise has been resolved).
     */
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


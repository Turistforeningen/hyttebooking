'use strict';
/**
 * @ngdoc service 
 * @name dntApp.cabinService
 * @description CabinService handles getting information about the cabins in the system from the backend. 
 * All data returned from the server should be json strings.
 * @requires $http 
**/
angular.module('dntApp').factory('cabinService', ['$http', '$q','$log', function ($http, $q, $log) {
	
	return {
		/**
	     * @ngdoc method
	     * @name dntApp.service#getCabins
	     * @methodOf dntApp.cabinService
	     * @returns {json} A list containing some properties for a subset of the cabins in the system.
	     */
		getCabins: function(page, pageSize) {
			var deferred = $q.defer();
			var url = '/api/admin/cabins?page=' + page + '&size=' + pageSize;
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
	     * @name dntApp.service#getCabinDetails
	     * @methodOf dntApp.cabinService
	     * @returns {json} A list of bookings belonging to the cabin specified by cabinId
	     */
		getCabinDetails: function(page, pageSize, cabinId) {
			var deferred = $q.defer();
			var url = '/api/admin/cabins/' + cabinId+'?page=' + page + '&size=' + pageSize;
			$http.get(url).success(function(data){
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
	     * @name dntApp.service#postCabin
	     * @methodOf dntApp.cabinService
	     * @returns {null} NOT SURE 
	     */
		postCabin: function(newCabin) {
			var deferred = $q.defer();
			var url = '/api/admin/cabins';
			$http.post(url, newCabin).success(function(data){
				deferred.resolve(data);
			}).error(function(error){
	
				deferred.reject(error);
			});
			return deferred.promise;
		},
		
		removePriceFromCabin: function(cabinId, priceId) {
			var deferred = $q.defer();
			var url = '/api/cabins/' + cabinId + '/prices/' + priceId;
			$http.delete(url).success(function(data){
				deferred.resolve(data);
			}).error(function(error){
	
				deferred.reject(error);
			});
			return deferred.promise;
		},
		
		addPriceFromCabin: function(cabinId, priceData) {
			var deferred = $q.defer();
			var url = '/api/cabins/' + cabinId + '/prices';
			$http.post(url, priceData).success(function(data){
				deferred.resolve(data);
			}).error(function(error){
	
				deferred.reject(error);
			});
			return deferred.promise;
		}
	}
	
}]);
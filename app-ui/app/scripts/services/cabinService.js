'use strict';
/**
 * @ngdoc service 
 * @name dntCommon.cabinService
 * @description CabinService handles getting information about the cabins in the system from the backend. 
 * All data returned from the server should be json strings.
 * @requires $http 
**/
angular.module('dntCommon').factory('cabinService', ['$http', '$q','$log', function ($http, $q, $log) {
	
	return {
		/**
	     * @ngdoc method
	     * @name dntCommon.service#getCabins
	     * @methodOf dntCommon.cabinService
	     * @param {Number} page What page of cabins to retrieve.
	     * @param {Number} pageSize size of page to be returned.
	     * @returns {Array} A list containing some properties for a subset of the cabins in the system.
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
	     * @name dntCommon.service#getCabinDetails
	     * @methodOf dntCommon.cabinService
	     * @param {Number} page What page of booking to retrieve.
	     * @param {Number} pageSize size of the page to be returned.
	     * @param {Number} cabinId id of cabin to request bookings from.
	     * @returns {Array} A list of bookings belonging to the cabin specified by cabinId
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
	     * @name dntCommon.service#getPrices
	     * @methodOf dntCommon.cabinService
	     * @param {Number} cabinId id of cabin to request the price matrix from.
	     * @returns {Array} A array containing the price matrix at cabin specified by cabinId
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
	     * @name dntCommon.service#postCabin
	     * @methodOf dntCommon.cabinService
	     * @param {JSON object} an object containing data needed to create a cabin at the back end.
	     * @returns {JSON object} Containing message and status when resolved or rejected. 
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
		
		/**
	     * @ngdoc method
	     * @name dntCommon.service#removePriceFromCabin
	     * @methodOf dntCommon.cabinService
	     * @param {Number} id of cabin to remove a price category from.
	     * @param {Number} id of price category to remove from cabin
	     * @returns {JSON object} Containing message and status when resolved or rejected 
	     */
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
		
		/**
	     * @ngdoc method
	     * @name dntCommon.service#removePriceFromCabin
	     * @methodOf dntCommon.cabinService
	     * @param {Number} id of cabin to add a price category to.
	     * @param {JSON object} the data needed to create a new price category for a cabin
	     * @returns {JSON object} Containing message and status when resolved or rejected 
	     */
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
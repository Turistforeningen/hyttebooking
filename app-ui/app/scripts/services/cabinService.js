'use strict';
/**
 * @ngdoc service 
 * @name dntApp.cabinService
 * @description CabinService handles getting information about the cabins in the system from the backend. 
 * All data returned from the server should be json strings.
 * @requires $http 
**/
angular.module('dntApp').service('cabinService', ['$http','$log', function ($http, $log) {

	/**
     * @ngdoc method
     * @name dntApp.service#getCabins
     * @methodOf dntApp.cabinService
     * @returns {json} A list containing some properties for a subset of the cabins in the system.
     */
	this.getCabins = function (page, pageSize) {
		var url = '/api/cabins?page=' + page + '&size=' + pageSize;
		return $http.get(url);
	};
	
	/**
     * @ngdoc method
     * @name dntApp.service#getCabinDetails
     * @methodOf dntApp.cabinService
     * @returns {json} A list of bookings belonging to the cabin specified by cabinId
     */
	this.getCabinDetails = function (page, pageSize, cabinId) {
		var url = '/api/cabins/' + cabinId+'?page=' + page + '&size=' + pageSize;
		return $http.get(url);
	};
	
	/**
     * @ngdoc method
     * @name dntApp.service#postCabin
     * @methodOf dntApp.cabinService
     * @returns {null} NOT SURE 
     */
	this.postCabin = function (newCabin) {
		var url = '/api/cabins';
		return $http.post(url, newCabin);
	};
}]);
'use strict';

/**
 * @ngdoc service 
 * @name dntApp.authorization
 * @description The authorization handles setup of DNT connect login, acquiring authentication token
 * and logout request.
 * @requires $http 
**/
angular.module('dntApp').factory('authorization', ['$http', function ($http) {

	return {
		newLogin: function() {
			return $http.get('api/login/setup');
		},
		checkLogin: function(data, hmac) {
			var credentials = {'data' : data, 'hmac' : hmac}
			return $http.post('/api/login/checkLogin', credentials);
		},
		
		logout: function () {
			return $http.post('/logout');
		},
	};
	
	
}]);

/**
 * @ngdoc service 
 * @name dntApp.appStateService
 * @requires $cookieStore 
 * @requires $location
 * @description The àppStateService` is a service used to persist user credentials and 
 * location of the app when redirecting to an external site. The data is persisted using cookieStore.
 *
**/
angular.module('dntApp').factory('appStateService', ['$cookieStore', '$location', function ($cookieStore, $location) {

	return {
		/**
	     * @ngdoc method
	     * @name dntApp.service#saveAttemptUrl
	     * @methodOf dntApp.appStateService
	     * @description Puts the apps location (url) into a browser cookie. Should be used
	     * before redirecting to an external site to keep the state of the app.
	     */
		saveAttemptUrl: function() {
			if($location.path().toLowerCase() != '/login') {
				$cookieStore.put('redirectUrl', $location.url());
			}
		},
		
		redirectToLogin: function() {
				$location.path('/login');
		},
		
		/**
	     * @ngdoc method
	     * @name dntApp.service#redirectToAttemptedUrl
	     * @methodOf dntApp.appStateService
	     * @description `redirectToAttemptedUrl` tries to redirect to a location
	     * retrieved from cookieStore. If no such location is found in cookieStore
	     * app is redirected to front page.
	     */
		redirectToAttemptedUrl: function() {
			$location.$$search = {};
			var url = $cookieStore.get('redirectUrl');
			$cookieStore.remove('redirectUrl');
			if(angular.isUndefined(url)) {
				$location.path('/');
			}
			else {
				$location.url(url);
			}
		},
		
		/**
	     * @ngdoc method
	     * @name dntApp.service#removeUserCredentials
	     * @methodOf dntApp.appStateService
	     * @description `removeUserCredentials` removes all user credentials stored in
	     * browser cookies.
	     */
		removeUserCredentials: function () {
			$cookieStore.remove('token');
			$cookieStore.remove('name');
			$cookieStore.remove('isAdmin');
			$cookieStore.put('email', email);
			$cookieStore.put('id', id);
		},
		
		/**
	     * @ngdoc method
	     * @name dntApp.service#insertUserCredentials
	     * @methodOf dntApp.appStateService
	     * @param {String} token authentication token sent with request to back end.
	     * @param {Number} id sherpa id of user
	     * @param {String} name full name of user
	     * @param {Boolean} isAdmin (only used in navbar to decide what drop down options to display) 
	     * @param {String} email email of user
	     * @description `insertUserCredentials` puts user credentials in a cookie, 
	     * mainly to keep a user from logging in all the time.
	     */
		insertUserCredentials: function (token, id, name, isAdmin, email) {
			$cookieStore.put('token', token);
			$cookieStore.put('id', id);
			$cookieStore.put('name', name);
			$cookieStore.put('isAdmin', isAdmin);
			$cookieStore.put('email', email);
		},
		
		/**
	     * @ngdoc method
	     * @name dntApp.service#insertUserCredentials
	     * @methodOf dntApp.appStateService
	     * @description `getUserCredentials` returns all user credentials in cookies.
	     * @returns {JSON object} user credentials like token, name, email and id.
	     */
		getUserCredentials: function () {
			var cred = {};
			cred.token = 	$cookieStore.get('token');
			cred.name = 	$cookieStore.get('name');
			cred.isAdmin = 	$cookieStore.get('isAdmin');
			cred.email = 	$cookieStore.get('email');
			cred.id = 		$cookieStore.get('id');
			return cred;
		}
		
	};
	
	
}]);

/**
 * @ngdoc service 
 * @name dntApp.httpInterceptor
 * @requires $q 
 * @requires appStateService 
 * @description httpInterceptor will intercept a unauthorized access, save location of
 * app and redirect user to /login view using àppStateService`
 * 
**/
angular.module('dntApp').factory('httpInterceptor', ['appStateService', '$q',  function httpInterceptor (appStateService, $q) {
	return function (promise) {
		var success = function (response) {
			return response;
		};

		var error = function (response) {
			if (response.status === 401) {
				appStateService.saveAttemptUrl();
				appStateService.redirectToLogin();
			}

			return $q.reject(response);
		};

		return promise.then(success, error);
	};
}]);


/**
 * @ngdoc service 
 * @name dntApp.api
 * @requires $http 
 * @requires appStateService 
 * @description Puts the authentication token into the header of http request done
 * by client. 
 * Token is retrieved either from cookieStore (logged in from a previous session)
 * or sent as a method parameter.
 * 
**/
angular.module('dntApp').factory('api', ['$http', 'appStateService', function ($http, appStateService) {
	return {
		init: function (token) {
			$http.defaults.headers.common['X-AUTH-TOKEN'] = token || appStateService.getUserCredentials().token;
		}
	};
}]);